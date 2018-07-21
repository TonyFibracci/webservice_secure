package com.nashtools.bot.framework;

public class FullActionAbstraction extends ActionAbstraction {
	
	private final static double[] preflop_4bet_sizes = new double[]{0.25, 0.5, 0.75};
	private final static double[] postflop_sizes = new double[]{0.25, 0.5, 0.75, 1, 1.5, 2};
	private final static double[] postflop_3bet_sizes = new double[]{0.25, 0.5, 0.75};
	private final static double[] postflop_firstin_big_pot_sizes = new double[]{0.25, 0.375, 0.5, 0.625, 0.75, 1, 1.5, 2};
	
	private double preflop_commitment_factor;
	private double flop_commitment_factor;
	private double turn_commitment_factor;
	private double river_commitment_factor;
	private boolean additional_actions_big_pot;
	
	public FullActionAbstraction(double preflop_commitment, double flop_commitment, double turn_commitment, double river_commitment, boolean additional_actions) {
		this.preflop_commitment_factor = preflop_commitment;
		this.flop_commitment_factor = flop_commitment;
		this.turn_commitment_factor = turn_commitment;
		this.river_commitment_factor = river_commitment;
		this.additional_actions_big_pot = additional_actions;
	}

	@Override
	public int get_actions(Game game, State state, Action[] actions) {
		  int num_actions = 0;
		  for( int a = 0; a < Constants.NUM_ACTION_TYPES; ++a ) {
		    Action action = new Action();
		    action.type = ActionType.values()[a];
		    action.size = 0;
		    if(action.type == ActionType.RAISE) {
		    	int[] min_raise_size = {0};
		    	int[] max_raise_size = {0};
		    	if(Game.raiseIsValid(game, state, min_raise_size, max_raise_size) != 0){
			/* Check for pot-size raise being valid.  First, get the pot size. */
			int pot = 0;
			for( int p = 0; p < game.numPlayers; ++p ) {
			  pot += state.spent[ p ];
			}
			/* Add amount needed to call.  This gives the size of a pot-sized raise */
			int player = Game.currentPlayer(game, state);
			int amount_to_call = state.maxSpent - state.spent[ player ];
			pot += amount_to_call;
	      /* Raise size is total amount of chips committed over all rounds
	       * after making the raise.
	       */
	      int round = state.round;
	      int pre_commitment = (int) (max_raise_size[0] * this.preflop_commitment_factor);
	      int flop_commitment = (int) (max_raise_size[0] * this.flop_commitment_factor);
	      int turn_commitment = (int) (max_raise_size[0] * this.turn_commitment_factor);
	      int river_commitment = (int) (max_raise_size[0] * this.river_commitment_factor);
	      int num_actions_round = state.numActions[state.round];
	      boolean three_bet_possible = (num_actions_round == 2 && state.action[round][0].type.ordinal() == 2)|| (num_actions_round == 3 && state.action[round][0].type.ordinal() == 1);
	      boolean four_bet_possible = (num_actions_round == 3 && state.action[round][0].type.ordinal() == 2) || (num_actions_round == 4 && state.action[round][0].type.ordinal() == 1);
	      boolean five_bet_possible = (num_actions_round == 4 && state.action[round][0].type.ordinal() == 2) || (num_actions_round == 5 && state.action[round][0].type.ordinal() == 1);
	      boolean big_pot_first_in = (additional_actions_big_pot && (num_actions_round == 0 || (num_actions_round == 1 && state.action[round][0].type.ordinal() == 1)) && pot >= 60);
	      if(round == 0){
	    	if(num_actions_round == 0){
	    		if(min_raise_size[0] < pre_commitment){
	    			actions[num_actions] = action.copy();
	    			actions[num_actions].size = min_raise_size[0];
	    			++num_actions;
	    		}
	    	}
	    	else if(four_bet_possible){
	    		for(int i = 0; i < preflop_4bet_sizes.length; i++){
	    			int size = (int) (pot * preflop_4bet_sizes[i] + ( state.spent[ player ] + amount_to_call ));
	    			if(size < pre_commitment && size >= min_raise_size[0]){
		    			actions[num_actions] = action.copy();
		    			actions[num_actions].size = size;
		    			++num_actions;
	    			}
	    		}
	    	}
	    	else if(five_bet_possible){
	    		
	    	}
	    	else{
	    		for(int i = 0; i < postflop_sizes.length; i++){
	    			int size = (int) (pot * postflop_sizes[i] + ( state.spent[ player ] + amount_to_call ));
	    			if(size < pre_commitment && size >= min_raise_size[0]){
		    			actions[num_actions] = action.copy();
		    			actions[num_actions].size = size;
		    			++num_actions;
	    			}
	    		}
	    	}

	    }
	    else if(round == 1){
	    	if(big_pot_first_in){
	    		for(int i = 0; i < postflop_firstin_big_pot_sizes.length; i++){
	    			int size = (int) (pot * postflop_firstin_big_pot_sizes[i] + ( state.spent[ player ] + amount_to_call ));
	    			if(size < flop_commitment && size >= min_raise_size[0]){
		    			actions[num_actions] = action.copy();
		    			actions[num_actions].size = size;
		    			++num_actions;
	    			}
	    		}
	    	}
	    	else if(five_bet_possible){
	    		
	    	}
	    	else if(three_bet_possible){
	    		for(int i = 0; i < postflop_3bet_sizes.length; i++){
	    			int size = (int) (pot * postflop_3bet_sizes[i] + ( state.spent[ player ] + amount_to_call ));
	    			if(size < flop_commitment && size >= min_raise_size[0]){
		    			actions[num_actions] = action.copy();
		    			actions[num_actions].size = size;
		    			++num_actions;
	    			}
	    		}
	    	}
	    	else if(four_bet_possible){
    			int size = (int) (pot * 0.25 + ( state.spent[ player ] + amount_to_call ));
    			if(size < flop_commitment && size >= min_raise_size[0]){
	    			actions[num_actions] = action.copy();
	    			actions[num_actions].size = size;
	    			++num_actions;
    			}
	    	}
	    	else{
	    		for(int i = 0; i < postflop_sizes.length; i++){
	    			int size = (int) (pot * postflop_sizes[i] + ( state.spent[ player ] + amount_to_call ));
	    			if(size < flop_commitment && size >= min_raise_size[0]){
		    			actions[num_actions] = action.copy();
		    			actions[num_actions].size = size;
		    			++num_actions;
	    			}
	    		}
	    	}
	    }
	    else if(round == 2){
	    	if(big_pot_first_in){
	    		for(int i = 0; i < postflop_firstin_big_pot_sizes.length; i++){
	    			int size = (int) (pot * postflop_firstin_big_pot_sizes[i] + ( state.spent[ player ] + amount_to_call ));
	    			if(size < turn_commitment && size >= min_raise_size[0]){
		    			actions[num_actions] = action.copy();
		    			actions[num_actions].size = size;
		    			++num_actions;
	    			}
	    		}
	    	}
	    	else if(five_bet_possible){
	    		
	    	}
	    	else if(three_bet_possible){
	    		for(int i = 0; i < postflop_3bet_sizes.length; i++){
	    			int size = (int) (pot * postflop_3bet_sizes[i] + ( state.spent[ player ] + amount_to_call ));
	    			if(size < turn_commitment && size >= min_raise_size[0]){
		    			actions[num_actions] = action.copy();
		    			actions[num_actions].size = size;
		    			++num_actions;
	    			}
	    		}
	    	}
	    	else if(four_bet_possible){
    			int size = (int) (pot * 0.25 + ( state.spent[ player ] + amount_to_call ));
    			if(size < turn_commitment && size >= min_raise_size[0]){
	    			actions[num_actions] = action.copy();
	    			actions[num_actions].size = size;
	    			++num_actions;
    			}
	    	}
	    	else{
	    		for(int i = 0; i < postflop_sizes.length; i++){
	    			int size = (int) (pot * postflop_sizes[i] + ( state.spent[ player ] + amount_to_call ));
	    			if(size < turn_commitment && size >= min_raise_size[0]){
		    			actions[num_actions] = action.copy();
		    			actions[num_actions].size = size;
		    			++num_actions;
	    			}
	    		}
	    	}    
	    }
	    else {
	    	if(big_pot_first_in){
	    		for(int i = 0; i < postflop_firstin_big_pot_sizes.length; i++){
	    			int size = (int) (pot * postflop_firstin_big_pot_sizes[i] + ( state.spent[ player ] + amount_to_call ));
	    			if(size < river_commitment && size >= min_raise_size[0]){
		    			actions[num_actions] = action.copy();
		    			actions[num_actions].size = size;
		    			++num_actions;
	    			}
	    		}
	    	}
	    	else if(five_bet_possible){
	    		
	    	}
	    	else if(three_bet_possible){
	    		for(int i = 0; i < postflop_3bet_sizes.length; i++){
	    			int size = (int) (pot * postflop_3bet_sizes[i] + ( state.spent[ player ] + amount_to_call ));
	    			if(size < river_commitment && size >= min_raise_size[0]){
		    			actions[num_actions] = action.copy();
		    			actions[num_actions].size = size;
		    			++num_actions;
	    			}
	    		}
	    	}
	    	else if(four_bet_possible){
    			int size = (int) (pot * 0.25 + ( state.spent[ player ] + amount_to_call ));
    			if(size < river_commitment && size >= min_raise_size[0]){
	    			actions[num_actions] = action.copy();
	    			actions[num_actions].size = size;
	    			++num_actions;
    			}
	    	}
	    	else{
	    		for(int i = 0; i < postflop_sizes.length; i++){
	    			int size = (int) (pot * postflop_sizes[i] + ( state.spent[ player ] + amount_to_call ));
	    			if(size < river_commitment && size >= min_raise_size[0]){
		    			actions[num_actions] = action.copy();
		    			actions[num_actions].size = size;
		    			++num_actions;
	    			}
	    		}
	    	}

	    }
	    /* Now add all-in */
	    actions[ num_actions ] = action.copy();
	    actions[ num_actions ].size = max_raise_size[0];
	    ++num_actions;
	      }

	    } else if(Game.isValidAction(game, state, false, action) != 0) {
		      /* Fold and call */
		      actions[ num_actions ] = action.copy();
		      ++num_actions;
		    }
	  }

	  return num_actions;

	}

}
