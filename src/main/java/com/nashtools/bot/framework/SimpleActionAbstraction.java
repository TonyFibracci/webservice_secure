package com.nashtools.bot.framework;

public class SimpleActionAbstraction extends ActionAbstraction {

	@Override
	public int get_actions(Game game, State state, Action[] actions) {
		int num_actions = 0;
		for (int a = 0; a < Constants.NUM_ACTION_TYPES; ++a) {
			Action action = new Action();
			action.type = ActionType.values()[a];
			action.size = 0;
			if (action.type == ActionType.RAISE) {
				int[] min_raise_size = { 0 };
				int[] max_raise_size = { 0 };
				if (Game.raiseIsValid(game, state, min_raise_size,max_raise_size) != 0) {
					int bb = game.blind[0];
					for(int i = min_raise_size[0]; i < max_raise_size[0]; i+=bb){
						actions[ num_actions ] = action;
						actions[ num_actions ].size = i;
						++num_actions;
					}

					/* Now add all-in */
					actions[num_actions] = action.copy();
					actions[num_actions].size = max_raise_size[0];
					++num_actions;
				}

			} else if (Game.isValidAction(game, state, false, action) != 0) {
				/* Fold and call */
				actions[num_actions] = action;
				++num_actions;
			}
		}

		return num_actions;
	}
}
