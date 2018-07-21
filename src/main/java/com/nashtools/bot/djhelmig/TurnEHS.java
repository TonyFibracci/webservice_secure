package com.nashtools.bot.djhelmig;

import java.util.Arrays;

public class TurnEHS extends Thread {

	private static final int AHEAD = 0, TIED = 1, BEHIND = 2;

	// our copy we may change
	private int evalhand[] = new int[6];
	private float table[];
	private int index = -1;

	public void setHand(int h[], float t[], int i) {

		System.arraycopy(h, 0, evalhand, 0, 6);
		table = t;
		index = i;
	}

	/*
	 * Gets us a fresh deck minus the 6 cards in deadcards.
	 * deck is sorted
	 */
	private static void turnGetDeck46(int deck[], int deadcards[]) {

		int di = 0;

		Arrays.sort(deadcards);

		for (int c = 0, idx = 0; c < 52; c++) {
			if (di < 6 && c == deadcards[di]) {
				di++;
				continue;
			}

			deck[idx] = c;
			idx++;
		}
	}

	private static void turnGetDeck44(int newdeck[], int olddeck[], int deadcards[]) {

		int di = 0;

		if (deadcards[0] > deadcards[1]) {
			int temp = deadcards[0];
			deadcards[0] = deadcards[1];
			deadcards[1] = temp;
		}

		for (int idx = 0, newidx = 0; idx < olddeck.length; idx++) {
			if (di < 2 && olddeck[idx] == deadcards[di]) {
				di++;
				continue;
			}

			newdeck[newidx] = olddeck[idx];
			newidx++;
		}
	}

	public void run() {

		float f = turnklaatuEHS(evalhand);
		table[index] = f;
	}

	public static int riverklaat[] = new int[45540];

	public static float turnklaatuEHS(int loudionhand[]) {

		int HS[] = new int[3];
		int HP[][] = new int[3][3];

		int ophand[] = new int[7];
		int myhand[] = new int[7];
		int orighand[] = new int[6];
		int opholecards[] = new int[2];

		for (int i = 0; i < 6; i++) {
			myhand[i] = loudionhand[i];
			orighand[i] = loudionhand[i];
		}

		/*
		 * Put board cards starting from index 2
		 */
		for (int i = 2; i < 6; i++) {
			ophand[i] = loudionhand[i];
		}

		int myValue = FastEval.eval6(myhand[0], myhand[1], myhand[2], myhand[3], myhand[4], myhand[5]);

		// deck with 46 cards in it
		int deck46[] = new int[46];
		// deck with 44 cards in it
		int deck44[] = new int[44];

		// our deck46 with known cards removed
		turnGetDeck46(deck46, orighand);

		/*
		 * First considered opponent card
		 */
		for (int c0 = 0; c0 < 46; c0++) {
			ophand[0] = deck46[c0];
			opholecards[0] = deck46[c0];

			/*
			 * Second considered opponent card
			 */
			for (int c1 = c0 + 1; c1 < 46; c1++) {
				ophand[1] = deck46[c1];
				opholecards[1] = deck46[c1];

				int opValue = FastEval.eval6(ophand[0], ophand[1], ophand[2], ophand[3], ophand[4],
						ophand[5]);
				int currentState;

				if (myValue > opValue) {
					currentState = AHEAD;
				} else if (myValue == opValue) {
					currentState = TIED;
				} else {
					currentState = BEHIND;
				}
				HS[currentState]++;

				turnGetDeck44(deck44, deck46, opholecards);

				/*
				 * For all rivers
				 */
				for (int b0 = 0; b0 < 44; b0++) {
					int board0 = deck44[b0];

					ophand[6] = board0;
					myhand[6] = board0;

					int myFullValue = FastEval.eval7(myhand[0], myhand[1], myhand[2], myhand[3], myhand[4],
							myhand[5], myhand[6]);
					int opFullValue = FastEval.eval7(ophand[0], ophand[1], ophand[2], ophand[3], ophand[4],
							ophand[5], ophand[6]);

					if (myFullValue > opFullValue) {
						HP[currentState][AHEAD]++;
					} else if (myFullValue == opFullValue) {
						HP[currentState][TIED]++;
					} else {
						HP[currentState][BEHIND]++;
					}
				}
			}
		}
		return getEHS.calculateEHS(HS, HP);
	}
}
