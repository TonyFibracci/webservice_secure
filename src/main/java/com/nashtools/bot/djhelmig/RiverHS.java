package com.nashtools.bot.djhelmig;

import java.util.Arrays;

public class RiverHS extends Thread {

	private static final int AHEAD = 0, TIED = 1, BEHIND = 2;

	private int evalhand[] = new int[7];
	private float table[];
	private int index = -1;

	public void setHand(int h[], float t[], int i) {

		System.arraycopy(h, 0, evalhand, 0, 7);
		table = t;
		index = i;
	}

	/*
	 * Gets us a fresh deck minus the 7 cards in deadcards.
	 * deck is sorted
	 */
	private static void riverGetDeck45(int deck[], int deadcards[]) {

		int di = 0;

		Arrays.sort(deadcards);

		for (int c = 0, idx = 0; c < 52; c++) {
			if (di < 7 && c == deadcards[di]) {
				di += 1;
				continue;
			}

			deck[idx] = c;
			idx++;
		}
	}

	public void run() {

		float f = riverklaatuHS(evalhand);
		table[index] = f;
	}

	public static float riverklaatuHS(int loudionhand[]) {

		int HS[] = new int[3];

		int ophand[] = new int[7];
		int myhand[] = new int[7];
		int orighand[] = new int[7];

		for (int i = 0; i < 7; i++) {
			myhand[i] = loudionhand[i];
			orighand[i] = loudionhand[i];
		}

		/*
		 * Put his hole cards in position {0,1}
		 */
		for (int i = 2; i < 7; i++) {
			ophand[i] = loudionhand[i];
		}

		int myValue = FastEval.eval7(myhand[0], myhand[1], myhand[2], myhand[3], myhand[4], myhand[5],
				myhand[6]);

		// deck with 45 cards in it
		int deck45[] = new int[45];

		// our deck45 with known cards removed
		riverGetDeck45(deck45, orighand);

		/*
		 * First considered opponent card
		 */
		for (int c0 = 0; c0 < 45; c0++) {
			ophand[0] = deck45[c0];

			/*
			 * Second considered opponent card
			 */
			for (int c1 = c0 + 1; c1 < 45; c1++) {
				ophand[1] = deck45[c1];

				int opValue = FastEval.eval7(ophand[0], ophand[1], ophand[2], ophand[3], ophand[4],
						ophand[5], ophand[6]);
				int currentState;

				if (myValue > opValue) {
					currentState = AHEAD;
				} else if (myValue == opValue) {
					currentState = TIED;
				} else {
					currentState = BEHIND;
				}
				HS[currentState] += 1;
			}
		}

		return (float) (HS[AHEAD] + HS[TIED] / 2) / (HS[AHEAD] + HS[TIED] + HS[BEHIND]);
	}
}
