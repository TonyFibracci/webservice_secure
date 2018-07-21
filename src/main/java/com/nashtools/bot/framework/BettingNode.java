package com.nashtools.bot.framework;

public abstract class BettingNode {

	BettingNode sibling;

	public BettingNode() {
		sibling = null;
	}

	static int integer_negation(int i) {
		if (i == 0)
			return 1;
		else
			return 0;
	}

	public abstract int get_num_choices();

	public abstract int get_player();

	public abstract int get_round();

	public abstract long get_soln_idx();

	public abstract BettingNode get_child();

	public BettingNode get_sibling() {
		return sibling;
	}

	static BettingNode init_betting_tree_r(State state, Game game,
			ActionAbstraction action_abs, long[] num_entries_per_bucket) {
		BettingNode node;
		if (state.finished) {
			/* Terminal node */

			boolean showdown = (state.playerFolded[0] || state.playerFolded[1]);
			int[] fold_value = new int[2];
			int money = -1;
			for (int p = 0; p < 2; ++p) {
				if (state.playerFolded[p]) {
					fold_value[p] = -1;
					money = state.spent[p];
				} else if (state.playerFolded[integer_negation(p)]) {
					fold_value[p] = 1;
					money = state.spent[integer_negation(p)];
				} else {
					fold_value[p] = 0;
					money = state.spent[p];
				}
			}
			node = new TerminalNode(showdown, fold_value, money);
			return node;
		}

		/* Choice node. First, compute number of different allowable actions */
		Action[] actions = new Action[Constants.MAX_ABSTRACT_ACTIONS];
		int num_choices = action_abs.get_actions(game, state, actions);

		/* Next, grab the index for this node into the regrets and avg_strategy */
		long soln_idx = num_entries_per_bucket[state.round];

		/* Update number of entries */
		num_entries_per_bucket[state.round] += num_choices;

		/* Recurse to create children */
		BettingNode first_child = null;
		BettingNode last_child = null;
		for (int a = 0; a < num_choices; ++a) {
			State new_state = new State(state);
			Game.doAction(game, actions[a], new_state);
			BettingNode child = init_betting_tree_r(new_state, game,
					action_abs, num_entries_per_bucket);
			if (last_child != null) {
				last_child.set_sibling(child);
			} else {
				first_child = child;
			}
			last_child = child;
		}
		assert (first_child != null);
		assert (last_child != null);

		/*
		 * Siblings are represented by a linked list, so the last child should
		 * have no sibling
		 */
		last_child.set_sibling(null);

		/* Create the InfoSetNode */

		node = new InfoSetNode(soln_idx, num_choices, Game.currentPlayer(game,
				state), state.round, first_child);
		return node;

	}

	public void set_sibling(BettingNode node) {
		this.sibling = node;
	}

}
