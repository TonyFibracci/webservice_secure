package com.nashtools.bot.djhelmig;
import java.util.Arrays;

public class FlopEHS extends Thread {

	private static final int AHEAD = 0, TIED = 1, BEHIND = 2;

	private int evalhand[] = new int[5];
	private float table[];
	private int index = -1;

	public void setHand(int h[], float t[], int i) {

		System.arraycopy(h, 0, evalhand, 0, 5);
		table = t;
		index = i;
	}

	public void run() {

		float f = flopklaatuEHS(evalhand);
		table[index] = f;
	}

	/*
	 * Gets us a fresh deck minus the 5 cards in deadcards.
	 * deck is sorted
	 */
	private static void flopGetDeck47(int deck[], int deadcards[]) {

		int di = 0;

		Arrays.sort(deadcards);

		for (int c = 0, idx = 0; c < 52; c++) {
			if (di < 5 && c == deadcards[di]) {
				di++;
				continue;
			}

			deck[idx] = c;
			idx++;
		}
	}

	/*
	 * Copy the cards from olddeck into newdeck except for the cards in deadcards
	 */
	private static void flopGetDeck45(int newdeck[], int olddeck[], int deadcards[]) {

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

	public static float flopklaatuEHS(int loudionhand[]) {

		int HS[] = new int[3];
		int HP[][] = new int[3][3];

		int ophand[] = new int[7];
		int myhand[] = new int[7];
		int orighand[] = new int[5];
		int opholecards[] = new int[2];

		for (int i = 0; i < 5; i++) {
			myhand[i] = loudionhand[i];
			orighand[i] = loudionhand[i];
		}

		/*
		 * Put his hole cards in position {0,1}
		 */
		for (int i = 2; i < 5; i++) {
			ophand[i] = loudionhand[i];
		}

		int myValue = FastEval.eval5(myhand[0], myhand[1], myhand[2], myhand[3], myhand[4]);

		// deck with 47 cards in it
		int deck47[] = new int[47];
		// deck with 45 cards in it
		int deck45[] = new int[45];

		// our deck47 with known cards removed
		flopGetDeck47(deck47, orighand);

		/*
		 * First considered opponent card
		 */
		for (int c0 = 0; c0 < 47; c0++) {
			ophand[0] = deck47[c0];
			opholecards[0] = deck47[c0];

			/*
			 * Second considered opponent card
			 */
			for (int c1 = c0 + 1; c1 < 47; c1++) {
				ophand[1] = deck47[c1];
				opholecards[1] = deck47[c1];

				int opValue = FastEval.eval5(ophand[0], ophand[1], ophand[2], ophand[3], ophand[4]);
				int currentState;

				if (myValue > opValue) {
					currentState = AHEAD;
				} else if (myValue == opValue) {
					currentState = TIED;
				} else {
					currentState = BEHIND;
				}
				HS[currentState]++;

				flopGetDeck45(deck45, deck47, opholecards);

				/*
				 * For all turns
				 */
				for (int b0 = 0; b0 < 45; b0++) {
					int board0 = deck45[b0];

					ophand[5] = board0;
					myhand[5] = board0;

					/*
					 * For all rivers
					 */
					for (int b1 = b0 + 1; b1 < 45; b1++) {
						int board1 = deck45[b1];

						ophand[6] = board1;
						myhand[6] = board1;

						int opFullValue = FastEval.eval7(ophand[0], ophand[1], ophand[2], ophand[3], ophand[4],
								ophand[5], ophand[6]);
						int myFullValue = FastEval.eval7(myhand[0], myhand[1], myhand[2], myhand[3], myhand[4],
								myhand[5], myhand[6]);

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
		}

		return getEHS.calculateEHS(HS, HP);
	}
}
