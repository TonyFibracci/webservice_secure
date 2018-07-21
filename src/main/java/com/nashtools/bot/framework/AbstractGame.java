package com.nashtools.bot.framework;

public class AbstractGame {

	public Game game;
	protected CardAbstraction card_abs;
	public ActionAbstraction action_abs;
	public BettingNode betting_tree_root;

	public AbstractGame(Game game, CardAbstraction cardAbstraction, ActionAbstraction action_abs) {
		this.game = game;
		this.action_abs = action_abs;
		
		  /* init num_entries_per_bucket to zero */
		  long[] num_entries_per_bucket = new long[Constants.MAX_ROUNDS];


		  /* process betting tree */
		  final State state = new State();
		  Game.initState( game, 0, state );
		  betting_tree_root = BettingNode.init_betting_tree_r( state, game, action_abs,
							   num_entries_per_bucket );

		  /* Create card abstraction */
		   card_abs = cardAbstraction;
	}

	void count_entries_r(final BettingNode node, long[] num_entries_per_bucket,
			long[] total_num_entries) {
		BettingNode child = node.get_child();

		if (child == null) {
			/* Terminal node */
			return;
		}

		int round = node.get_round();
		int num_choices = node.get_num_choices();

		/* Update entries counts */
		num_entries_per_bucket[round] += num_choices;
		int buckets = card_abs.num_buckets(game, node);
		total_num_entries[round] += buckets * num_choices;

		/* Recurse */
		for (int c = 0; c < num_choices; ++c) {
			count_entries_r(child, num_entries_per_bucket, total_num_entries);
			child = child.get_sibling();
		}
	}

	void count_entries(long[] num_entries_per_bucket, long[] total_num_entries) {
		count_entries_r(betting_tree_root, num_entries_per_bucket,
				total_num_entries);
	}
}
