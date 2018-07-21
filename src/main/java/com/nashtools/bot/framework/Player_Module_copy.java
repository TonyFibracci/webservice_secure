package com.nashtools.bot.framework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

public class Player_Module_copy {

	boolean verbose;
	public AbstractGame ag;
	Random random = new Random();
	FileChannel fc;
	public long[] num_entries_per_bucket;
	public long[] total_num_entries;
	
	public Player_Module_copy(String path, Game game, CardAbstraction cardAbstraction, ActionAbstraction action_abs){
		this.ag = null;
		this.verbose = false;
		
	
	  /* Initialize abstract game, rng from parameters */
	  ag = new AbstractGame(game, cardAbstraction, action_abs);
	  
	  /* Next, count the number of entries required per round to store the entries */
	 num_entries_per_bucket = new long[Constants.MAX_ROUNDS ];
	 total_num_entries = new long[Constants.MAX_ROUNDS];

	  ag.count_entries( num_entries_per_bucket, total_num_entries );


	  /* Now MMAP the entire file */
	  RandomAccessFile memoryMappedFile = null;
	try {
		memoryMappedFile = new RandomAccessFile(path, "r");
	} catch (FileNotFoundException e) {
		throw new RuntimeException(e);
	}
	  fc = memoryMappedFile.getChannel();

	}
	
	long get_entry_index(int bucket, long soln_idx, long num_entries_per_bucket) {
		return (num_entries_per_bucket * bucket) + soln_idx;
	}	
	

	void get_default_action_probs(State state, double[] action_probs) {
		/* Default will be always call */

		/* Get the abstract actions */
		Action[] actions = new Action[Constants.MAX_ABSTRACT_ACTIONS];
		int num_choices = ag.action_abs.get_actions(ag.game, state, actions);

		/* Find the call action */
		for (int a = 0; a < num_choices; ++a) {
			if (actions[a].type == ActionType.CALL) {
				action_probs[a] = 1.0;
				return;
			}
		}

		/*
		 * Still haven't returned? This means we couldn't find a call action, so
		 * we must be dealing with a very weird action abstraction. Let's just
		 * always play the first action then by default.
		 */
		action_probs[0] = 1.0;
	}
	

	public void get_action_probs(State state, double[] action_probs, int bucket) {
		/*
		 * Initialize action probs to the default in case we must abort early
		 * for one of several reasons
		 */
		get_default_action_probs(state, action_probs);


		/* Find the current node from the sequence of actions in state */
		BettingNode node = ag.betting_tree_root;
		State old_state = new State();
		Game.initState(ag.game, 0, old_state);
		if (verbose) {
			System.out.println("Translated abstract state: ");
		}
		for (int r = 0; r <= state.round; ++r) {
			for (int a = 0; a < state.numActions[r]; ++a) {
				final Action real_action = state.action[r][a];
				Action[] abstract_actions = new Action[Constants.MAX_ABSTRACT_ACTIONS];
				int num_actions = ag.action_abs.get_actions(ag.game, old_state,
						abstract_actions);
				if (num_actions != node.get_num_choices()) {
					if (verbose) {
						System.out
								.println("Number of actions %d does not match number "
										+ "of choices %d\n");
					}
					return;
				}
				int choice;
				if ((ag.game.bettingType == BettingType.NOLIMITBETTING)
						&& (real_action.type == ActionType.RAISE)) {
					/*
					 * Need to translate raise action into a raise that we
					 * understand. What fun... For now, let's just use soft
					 * translation with geometric similarity as described by
					 * [Schnizlein, Bowling, and Szafron; IJCAI 2009], and let's
					 * just ignore any issues arising from repeated translation
					 * of successive decisions. We'll also use stack ratios
					 * rather than pot fraction ratios just to keep life a
					 * little easier. This is a fairly naive approach to
					 * translation and can likely be improved.
					 */

					/*
					 * First, find the smallest abstract raise greater than or
					 * equal to the real raise size (upper), and the largest
					 * abstract raise less than or equal to the real raise size
					 * (lower).
					 */
					int lower = 0, upper = ag.game.stack[node.get_player()] + 1;
					int lower_choice = -1, upper_choice = -1;
					for (int i = 0; i < num_actions; ++i) {
						if (abstract_actions[i].type == ActionType.RAISE) {
							if ((abstract_actions[i].size <= real_action.size)
									&& (abstract_actions[i].size >= lower)) {
								lower = abstract_actions[i].size;
								lower_choice = i;
							}
							if ((abstract_actions[i].size >= real_action.size)
									&& (abstract_actions[i].size <= upper)) {
								upper = abstract_actions[i].size;
								upper_choice = i;
							}
						}
					}

					/*
					 * 4 cases to consider depending on the lower and upper
					 * found above
					 */
					if (lower == upper) {
						/* We have an exact match! */
						choice = lower_choice; /*
												 * Should be the same as
												 * upper_choice
												 */
					} else if (lower_choice == -1) {
						/*
						 * No abstract raise less than or equal to real action
						 * raise
						 */
						if (upper_choice == -1) {
							if (verbose) {
								System.out
										.println("Could not translate at round %d turn %d\n");
							}
							return;
						}
						choice = upper_choice;
					} else if (upper_choice == -1) {
						/*
						 * No abstract raise greater than or equal to real
						 * action raise
						 */
						if (lower_choice == -1) {
							if (verbose) {
								System.out
										.println("Could not translate at round %d turn %d\n");
							}
							return;
						}
						choice = lower_choice;
					} else {
						/*
						 * Get similarity metric values for lower and upper
						 * raises
						 */
						double lower_sim = ((1.0 * lower / real_action.size) - (1.0 * lower / upper))
								/ (1 - (1.0 * lower / upper));

						double upper_sim = ((1.0 * real_action.size / upper) - (1.0 * lower / upper))
								/ (1 - (1.0 * lower / upper));

						/*
						 * Throw a dart and probabilistically choose lower or
						 * upper
						 */
						double dart = random.nextDouble();
						if (dart < (lower_sim / (lower_sim + upper_sim))) {
							choice = lower_choice;
						} else {
							choice = upper_choice;
						}
					}

				} else {
					/*
					 * Limit game or non-raise action. Just match the real
					 * action.
					 */
					for (choice = 0; choice < num_actions; ++choice) {
						if (abstract_actions[choice].type == real_action.type) {
							break;
						}
					}
					if (choice >= num_actions) {
						if (verbose) {
							System.out.print("Unable to translate action at round %d, turn %d; " + "actions available are:");
							// for( int i = 0; i < num_actions; ++i ) {
							// char action_str[ PATH_LENGTH ];
							// printAction( ag.game, abstract_actions[ i ],
							// PATH_LENGTH,
							// action_str );
							// System.out.println( " %s", action_str );
							// }
							System.out.println("\n");
						}
						return;
					}
				}
				/* Move the current node and old_state along */
				node = node.get_child();
				for (int i = 0; i < choice; ++i) {
					node = node.get_sibling();
					if (node == null) {
						if (verbose) {
							System.out.println("Ran out of siblings for choice %d\n");
						}
						return;
					}
				}
				if (node.get_child() == null) {
					if (verbose) {
						System.out.println(" Abstract game over\n");
					}
					return;
				}
				Game.doAction(ag.game, abstract_actions[choice], old_state);
			}
		}

		/* Bucket the cards */
		if (bucket == -1) {
			bucket = ag.card_abs.get_bucket(ag.game, node, state.boardCards, state.holeCards);
		}
		if (verbose) {
			System.out.println(" Bucket: " + bucket);
		}

		/* Check for problems */
		if (Game.currentPlayer(ag.game, state) != node.get_player()) {
			if (verbose) {
				System.out.println("Abstract player does not match current player\n");
			}
			return;
		}
		if (state.round != node.get_round()) {
			if (verbose) {
				System.out.println("Abstract round does not match current round\n");
			}
			return;
		}

		/* Get the positive entries at this information set */
		int num_choices = node.get_num_choices();
		long soln_idx = node.get_soln_idx();
		int round = node.get_round();
		long[] pos_entries = new long[num_choices];
		
		/* Get start position and byte size*/
		long entry_index = 0;
		for(int i = 0; i <= round; i++){
			entry_index += 4;
		}
		for(int i = 0; i < round; i++){
			if(i == 0)
				entry_index += (total_num_entries[i] * Constants.PREFLOP_BYTE_SIZE);
			else
				entry_index += (total_num_entries[i] * Constants.POSTFLOP_BYTE_SIZE);
		}
		long inner_entry_index = 0;
		if(round == 0)
			inner_entry_index = get_entry_index(bucket, soln_idx, num_entries_per_bucket[round]) * Constants.PREFLOP_BYTE_SIZE;
		else
			inner_entry_index = get_entry_index(bucket, soln_idx, num_entries_per_bucket[round]) * Constants.POSTFLOP_BYTE_SIZE;
		long bufferPos = entry_index + inner_entry_index ;
		long byteSize = 0;
		if(round == 0)
			byteSize = num_choices * Constants.PREFLOP_BYTE_SIZE;
		else
			byteSize = num_choices * Constants.POSTFLOP_BYTE_SIZE;
		
		/* Load strategy and calculate sum*/
		MappedByteBuffer out;
		try {
			out = fc.map(FileChannel.MapMode.READ_ONLY, bufferPos, byteSize);
			out.order(ByteOrder.LITTLE_ENDIAN);
			if(round == 0 && Constants.PREFLOP_BYTE_SIZE == 8){
				LongBuffer lg = out.asLongBuffer();
				long l[] = new long[lg.capacity()];
				lg.get(l);
				long[] strategies = l;
				/* Zero out negative values and store in the returned array */
				long sum_values = 0;
				for (int c = 0; c < num_choices; ++c) {
					if (strategies[c] <= 0)
						strategies[c] = 0;
					pos_entries[c] = strategies[c];
					sum_values += strategies[c];
				}
				/* Get the abstract game action probabilities */
				if (sum_values == 0) {
					if (verbose) {
						System.out.println("ALL POSITIVE ENTRIES ARE ZERO\n");
					}
					return;
				}
				for (int c = 0; c < num_choices; ++c) {
					action_probs[c] = 1.0 * pos_entries[c] / sum_values;
				}				
			}
			else if(round == 0 && Constants.PREFLOP_BYTE_SIZE == 4){
				IntBuffer lg = out.asIntBuffer();
				int l[] = new int[lg.capacity()];
				lg.get(l);
				int[] strategies = l;
				/* Zero out negative values and store in the returned array */
				long sum_values = 0;
				for (int c = 0; c < num_choices; ++c) {
					if (strategies[c] < 0)
						strategies[c] = 0;
					pos_entries[c] = strategies[c];
					sum_values += strategies[c];
				}	
				/* Get the abstract game action probabilities */
				if (sum_values == 0) {
					if (verbose) {
						System.out.println("ALL POSITIVE ENTRIES ARE ZERO\n");
					}
					return;
				}
				for (int c = 0; c < num_choices; ++c) {
					action_probs[c] = 1.0 * pos_entries[c] / sum_values;
				}
			}
			else if(Constants.POSTFLOP_BYTE_SIZE == 4){
				IntBuffer lg = out.asIntBuffer();
				int l[] = new int[lg.capacity()];
				lg.get(l);
				int[] strategies = l;
				/* Zero out negative values and store in the returned array */
				long sum_values = 0;
				for (int c = 0; c < num_choices; ++c) {
					if (strategies[c] < 0)
						strategies[c] = 0;
					pos_entries[c] = strategies[c];
					sum_values += strategies[c];
				}	
				/* Get the abstract game action probabilities */
				if (sum_values == 0) {
					if (verbose) {
						System.out.println("ALL POSITIVE ENTRIES ARE ZERO\n");
					}
					return;
				}
				for (int c = 0; c < num_choices; ++c) {
					action_probs[c] = 1.0 * pos_entries[c] / sum_values;
				}
			}
			else{
				byte l[] = new byte[out.capacity()];
				int[] strategies = new int[out.capacity()];
				out.get(l);
				for (int c = 0; c < num_choices; ++c) {
					strategies[c] = unsignedToBytes(l[c]);
				}
				/* Zero out negative values and store in the returned array */
				long sum_values = 0;
				for (int c = 0; c < num_choices; ++c) {
					if (strategies[c] < 0)
						strategies[c] = 0;
					pos_entries[c] = strategies[c];
					sum_values += strategies[c];
				}	
				/* Get the abstract game action probabilities */
				if (sum_values == 0) {
					if (verbose) {
						System.out.println("ALL POSITIVE ENTRIES ARE ZERO\n");
					}
					return;
				}
				for (int c = 0; c < num_choices; ++c) {
					action_probs[c] = 1.0 * pos_entries[c] / sum_values;
				}				
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int unsignedToBytes(byte b) {
		return b & 0xFF;
	}
	
	
	public Action get_action(State state){
		double[] action_probs = new double[Constants.MAX_ABSTRACT_ACTIONS];
		get_action_probs(state, action_probs, -1);
		Action[] actions = new Action[Constants.MAX_ABSTRACT_ACTIONS];
		int num_choices = ag.action_abs.get_actions(ag.game, state, actions);
		double dart = random.nextDouble();
		int a;
		for(a = 0; a < num_choices - 1; a++){
			if(dart < action_probs[a])
				break;
			dart -= action_probs[a];
		}
		return actions[a];
	}
}
