package com.nashtools.bot.framework;

public class NullActionAbstraction extends ActionAbstraction {

	public int get_actions(Game game, State state, Action[] actions) {
		int num_actions = 0;
		boolean error = false;
		for (int a = 0; a < Constants.NUM_ACTION_TYPES; ++a) {
			Action action = new Action();
			action.type = ActionType.values()[a];
			action.size = 0;
			if (action.type == ActionType.RAISE) {
				int[] min_raise_size = {0};
				int[] max_raise_size = {0};
				if (Game.raiseIsValid(game, state, min_raise_size,
						max_raise_size) != 0) {
					if (num_actions + (max_raise_size[0] - min_raise_size[0] + 1) > Constants.MAX_ABSTRACT_ACTIONS) {
						error = true;
						break;
					}
					for (int s = min_raise_size[0]; s <= max_raise_size[0]; ++s) {
						actions[num_actions] = action;
						actions[num_actions].size = s;
						++num_actions;
					}
				}
			} else if (Game.isValidAction(game, state, false, action) != 0) {
				/*
				 * If you hit this assert, there are too many abstract actions
				 * allowed. Either coarsen the betting abstraction or increase
				 * MAX_ABSTRACT_ACTIONS in constants.hpp
				 */
				if (num_actions >= Constants.MAX_ABSTRACT_ACTIONS) {
					error = true;
					break;
				}
				actions[num_actions] = action;
				++num_actions;
			}

		}
		/*
		 * If you hit this assert, there are too many abstract actions allowed.
		 * Either coarsen the betting abstraction or increase
		 * MAX_ABSTRACT_ACTIONS in constants.hpp
		 */
		assert (!error);

		return num_actions;
	}
}
