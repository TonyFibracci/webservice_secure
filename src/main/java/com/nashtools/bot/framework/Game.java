package com.nashtools.bot.framework;

public abstract class Game {
	public int[] stack, blind, raiseSize;
	BettingType bettingType;
	int numPlayers, numRounds, numSuits, numRanks, numHoleCards;
	int[] firstPlayer, maxRaises, numBoardCards;

	static int nextPlayer(Game game, State state, int actingPlayer) {
		int n;

		n = actingPlayer;
		do {
			n = (n + 1) % game.numPlayers;
		} while (state.playerFolded[n] || state.spent[n] >= game.stack[n]);

		return n;
	}

	public static int currentPlayer(Game game, State state) {
		/* if action has already been made, compute next player from last player */
		if (state.numActions[state.round] > 0) {
			return nextPlayer(
					game,
					state,
					state.actingPlayer[state.round][state.numActions[state.round] - 1]);
		}

		/*
		 * first player in a round is determined by the game and round use
		 * nextPlayer() because firstPlayer[round] might be unable to act
		 */
		return nextPlayer(game, state, game.firstPlayer[state.round]
				+ game.numPlayers - 1);
	}

	static int numRaises(final State state) {
		int i;
		int ret;

		ret = 0;
		for (i = 0; i < state.numActions[state.round]; ++i) {
			if (state.action[state.round][i].type == ActionType.RAISE) {
				++ret;
			}
		}

		return ret;
	}

	static int raiseIsValid(final Game game, final State curState, int[] minSize,
			int[] maxSize) {
		int p;

		//System.out.println("numRaises:" + numRaises(curState) + " numActions:" + curState.numActions[curState.round]);
		if (numRaises(curState) >= game.maxRaises[curState.round]) {
			/* already made maximum number of raises */

			return 0;
		}

		if (curState.numActions[curState.round] + game.numPlayers > Constants.MAX_NUM_ACTIONS) {
			/* 1 raise + NUM PLAYERS-1 calls is too many actions */

			System.out
					.println("WARNING: #actions in round is too close to MAX_NUM_ACTIONS, forcing call/fold\n");
			return 0;
		}

		if (numActingPlayers(game, curState) <= 1) {
			/*
			 * last remaining player can't bet if there's no one left to call
			 * (this check is needed if the 2nd last player goes all in, and the
			 * last player has enough stack left to bet)
			 */

			return 0;
		}

		if (game.bettingType != BettingType.NOLIMITBETTING) {
			/* if it's not no-limit betting, don't worry about sizes */

			minSize[0] = 0;
			maxSize[0] = 0;
			return 1;
		}

		p = currentPlayer(game, curState);
		minSize[0] = curState.minNoLimitRaiseTo;
		maxSize[0] = game.stack[p];

		/* handle case where remaining player stack is too small */
		if (minSize[0] > game.stack[p]) {
			/* can't handle the minimum bet size - can we bet at all? */

			if (curState.maxSpent >= game.stack[p]) {
				/* not enough money to increase current bet */

				return 0;
			} else {
				/* can raise by going all-in */

				minSize[0] = maxSize[0];
				return 1;
			}
		}

		return 1;
	}

	public static void initState(Game game, final int handId, State state) {
		int p, r;

		state.handId = handId;

		state.maxSpent = 0;
		for (p = 0; p < game.numPlayers; ++p) {

			state.spent[p] = game.blind[p];
			if (game.blind[p] > state.maxSpent) {

				state.maxSpent = game.blind[p];
			}
		}

		if (game.bettingType == BettingType.NOLIMITBETTING) {
			/* no-limit games need to keep track of the minimum bet */

			if (state.maxSpent > 0) {
				/*
				 * we'll have to call the big blind and then raise by that
				 * amount, so the minimum raise-to is 2*maximum blinds
				 */

				state.minNoLimitRaiseTo = state.maxSpent * 2;
			} else {
				/* need to bet at least one chip, and there are no blinds/ante */

				state.minNoLimitRaiseTo = 1;
			}
		} else {
			/* no need to worry about minimum raises outside of no-limit games */

			state.minNoLimitRaiseTo = 0;
		}

		for (p = 0; p < game.numPlayers; ++p) {

			state.spent[p] = game.blind[p];

			if (game.blind[p] > state.maxSpent) {
				state.maxSpent = game.blind[p];
			}

			state.playerFolded[p] = false;
		}

		for (r = 0; r < game.numRounds; ++r) {

			state.numActions[r] = 0;
		}

		state.round = 0;

		state.finished = false;
	}

	public static int isValidAction(Game game, State curState,
			boolean tryFixing, Action action) {
		int[] min = {0};
		int[] max = {0};
		int p;

		if (curState.finished || action.type == ActionType.INVALID) {
			return 0;
		}

		p = currentPlayer(game, curState);

		if (action.type == ActionType.INVALID) {

			if (raiseIsValid(game, curState, min, max) == 0) {
				/* there are no valid raise sizes */

				return 0;
			}

			if (game.bettingType == BettingType.NOLIMITBETTING) {
				/* no limit games have a size */

				if (action.size < min[0]) {
					/* bet size is too small */

					if (!tryFixing) {

						return 0;
					}
					// fprintf( stderr,
					// "WARNING: raise of %d increased to %d\n",
					// action.size, min );
					action.size = min[0];
				} else if (action.size > max[0]) {
					/* bet size is too big */

					if (!tryFixing) {

						return 0;
					}
					// fprintf( stderr,
					// "WARNING: raise of %d decreased to %d\n",
					// action.size, max );
					action.size = max[0];
				}
			} else {

			}
		} else if (action.type == ActionType.FOLD) {

			if (curState.spent[p] == curState.maxSpent
					|| curState.spent[p] == game.stack[p]) {
				/* player has already called all bets, or is all-in */

				return 0;
			}

			if (action.size != 0) {

				// fprintf( stderr, "WARNING: size given for fold\n" );
				action.size = 0;
			}
		} else {
			/* everything else */

			if (action.size != 0) {

				// fprintf( stderr,
				// "WARNING: size given for something other than a no-limit raise\n"
				// );
				action.size = 0;
			}
		}

		return 1;
	}

	static int numFolded(final Game game, final State state) {
		int p;
		int ret;

		ret = 0;
		for (p = 0; p < game.numPlayers; ++p) {
			if (state.playerFolded[p]) {
				++ret;
			}
		}

		return ret;
	}

	static int numCalled(final Game game, final State state) {
		int i;
		int ret, p;

		ret = 0;
		for (i = state.numActions[state.round]; i > 0; --i) {

			p = state.actingPlayer[state.round][i - 1];

			if (state.action[state.round][i - 1].type == ActionType.RAISE) {
				/* player initiated the bet, so they've called it */

				if (state.spent[p] < game.stack[p]) {
					/* player is not all-in, so they're still acting */

					++ret;
				}

				/* this is the start of the current bet, so we're finished */
				return ret;
			} else if (state.action[state.round][i - 1].type == ActionType.CALL) {

				if (state.spent[p] < game.stack[p]) {
					/* player is not all-in, so they're still acting */

					++ret;
				}
			}
		}

		return ret;
	}

	static int numActingPlayers(final Game game, final State state) {
		int p;
		int ret;

		ret = 0;
		for (p = 0; p < game.numPlayers; ++p) {
			if (state.playerFolded[p] == false
					&& state.spent[p] < game.stack[p]) {
				++ret;
			}
		}

		return ret;
	}

	public static void doAction(final Game game, final Action action,
			State state) {
		int p = currentPlayer(game, state);

		assert (state.numActions[state.round] < Constants.MAX_NUM_ACTIONS);

		state.action[state.round][state.numActions[state.round]] = action;
		state.actingPlayer[state.round][state.numActions[state.round]] = p;
		++state.numActions[state.round];

		switch (action.type) {
		case FOLD:

			state.playerFolded[p] = true;
			break;

		case CALL:

			if (state.maxSpent > game.stack[p]) {
				/* calling puts player all-in */

				state.spent[p] = game.stack[p];
			} else {
				/* player matches the bet by spending same amount of money */

				state.spent[p] = state.maxSpent;
			}
			break;

		case RAISE:

			if (game.bettingType == BettingType.NOLIMITBETTING) {
				/* no-limit betting uses size in action */

				assert (action.size > state.maxSpent);
				assert (action.size <= game.stack[p]);

				/*
				 * next raise must call this bet, and raise by at least this
				 * much
				 */
				if (action.size + action.size - state.maxSpent > state.minNoLimitRaiseTo) {

					state.minNoLimitRaiseTo = action.size + action.size
							- state.maxSpent;
				}
				state.maxSpent = action.size;
			} else {
				/* limit betting uses a fixed amount on top of current bet size */

				if (state.maxSpent + game.raiseSize[state.round] > game.stack[p]) {
					/* raise puts player all-in */

					state.maxSpent = game.stack[p];
				} else {
					/* player raises by the normal limit size */

					state.maxSpent += game.raiseSize[state.round];
				}
			}

			state.spent[p] = state.maxSpent;
			break;

		default:
			System.out.println("ERROR: trying to do invalid action %d");

		}

		/* see if the round or game has ended */
		if (numFolded(game, state) + 1 >= game.numPlayers) {
			/* only one player left - game is immediately over, no showdown */

			state.finished = true;
		} else if (numCalled(game, state) >= numActingPlayers(game, state)) {
			/* >= 2 non-folded players, all acting players have called */

			if (numActingPlayers(game, state) > 1) {
				/* there are at least 2 acting players */

				if (state.round + 1 < game.numRounds) {
					/* active players move onto next round */

					++state.round;

					/*
					 * minimum raise-by is reset to minimum of big blind or 1
					 * chip
					 */
					state.minNoLimitRaiseTo = 1;
					for (p = 0; p < game.numPlayers; ++p) {

						if (game.blind[p] > state.minNoLimitRaiseTo) {

							state.minNoLimitRaiseTo = game.blind[p];
						}
					}

					/*
					 * we finished at least one round, so raise-to = raise-by +
					 * maxSpent
					 */
					state.minNoLimitRaiseTo += state.maxSpent;
				} else {
					/* no more betting rounds, so we're totally finished */

					state.finished = true;
				}
			} else {
				/*
				 * not enough players for more betting, but still need a
				 * showdown
				 */

				state.finished = true;
				state.round = game.numRounds - 1;
			}
		}
	}
}
