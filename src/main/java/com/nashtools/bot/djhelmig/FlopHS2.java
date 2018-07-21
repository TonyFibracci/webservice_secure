package com.nashtools.bot.djhelmig;

import java.util.Arrays;

public class FlopHS2 extends Thread {

	private int evalhand[] = new int[5];
	private float table[];
	private int index = -1;

	public void setHand(int h[], float t[], int i) {

		System.arraycopy(h, 0, evalhand, 0, 5);
		table = t;
		index = i;
	}

	public void run() {

		float f = flopklaatuHS2(evalhand);
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

	public static float flopklaatuHS2(int loudionhand[]) {

		int ophand[] = new int[7];
		int myhand[] = new int[7];
		int orighand[] = new int[5];
		int boardcards[] = new int[2];

		long wins = 0;
		long loss = 0;
		long ties = 0;

		int count = 0;

		double hs = 0;

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

		// deck with 47 cards in it
		int deck47[] = new int[47];
		// deck with 45 cards in it
		int deck45[] = new int[45];

		// our deck47 with known cards removed
		flopGetDeck47(deck47, orighand);

		/*
		 * For all turns
		 */
		for (int b0 = 0; b0 < 47; b0++) {
			int board0 = deck47[b0];
			boardcards[0] = board0;

			ophand[5] = board0;
			myhand[5] = board0;

			/*
			 * For all rivers
			 */
			for (int b1 = b0 + 1; b1 < 47; b1++) {
				int board1 = deck47[b1];
				boardcards[1] = board1;

				flopGetDeck45(deck45, deck47, boardcards);

				ophand[6] = board1;
				myhand[6] = board1;

				wins = 0;
				loss = 0;
				ties = 0;

				// OPPONENT CARDS
				for (int c0 = 0; c0 < 45; c0++) {
					ophand[0] = deck45[c0];
					for (int c1 = c0 + 1; c1 < 45; c1++) {
						ophand[1] = deck45[c1];

						int opFullValue = FastEval.eval7(ophand[0], ophand[1], ophand[2], ophand[3], ophand[4],
								ophand[5], ophand[6]);
						int myFullValue = FastEval.eval7(myhand[0], myhand[1], myhand[2], myhand[3], myhand[4],
								myhand[5], myhand[6]);

						if (myFullValue > opFullValue)
							wins++;
						else if (myFullValue == opFullValue)
							ties++;
						else
							loss++;

					}
				}
				count++;

				double ret = (wins + (ties / 2.0)) / (wins + ties + loss);
				hs += ret * ret;
			}
		}

		return (float) (hs / count);
	}
}
