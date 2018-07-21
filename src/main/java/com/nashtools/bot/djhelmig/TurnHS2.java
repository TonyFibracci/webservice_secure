package com.nashtools.bot.djhelmig;

import java.util.Arrays;

public class TurnHS2 extends Thread {

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

	/*
	 * Copy the cards from olddeck into newdeck except for the cards in deadcards
	 */
	private static void turnGetDeck45(int newdeck[], int olddeck[], int deadcard) {

		int di = 0;

		for (int idx = 0, newidx = 0; idx < olddeck.length; idx++) {
			if (di < 2 && olddeck[idx] == deadcard) {
				di++;
				continue;
			}

			newdeck[newidx] = olddeck[idx];
			newidx++;
		}
	}

	public void run() {

		float f = turnklaatuHS2(evalhand);
		table[index] = f;
	}

	public static int riverklaat[] = new int[45540];

	public static float turnklaatuHS2(int loudionhand[]) {

		double hs = 0;
		int wins = 0;
		int loss = 0;
		int ties = 0;
		int count = 0;

		int ophand[] = new int[7];
		int myhand[] = new int[7];
		int orighand[] = new int[6];

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

		// deck with 46 cards in it
		int deck46[] = new int[46];
		// deck with 44 cards in it
		int deck45[] = new int[45];

		// our deck46 with known cards removed
		turnGetDeck46(deck46, orighand);

		/*
		 * For all rivers
		 */
		for (int b0 = 0; b0 < 46; b0++) {
			int board0 = deck46[b0];

			ophand[6] = board0;
			myhand[6] = board0;

			turnGetDeck45(deck45, deck46, board0);

			wins = 0;
			loss = 0;
			ties = 0;

			// OPP CARDS
			for (int c0 = 0; c0 < 45; c0++) {
				ophand[0] = deck45[c0];
				for (int c1 = c0 + 1; c1 < 45; c1++) {
					ophand[1] = deck45[c1];

					int myFullValue = FastEval.eval7(myhand[0], myhand[1], myhand[2], myhand[3], myhand[4],
							myhand[5], myhand[6]);
					int opFullValue = FastEval.eval7(ophand[0], ophand[1], ophand[2], ophand[3], ophand[4],
							ophand[5], ophand[6]);

					if (myFullValue > opFullValue)
						wins++;
					else if (myFullValue == opFullValue)
						ties++;
					else
						loss++;

				}
			}

			double ret = (wins + (ties / 2.0)) / (wins + ties + loss);
			hs += ret * ret;
			count++;

		}
		// System.out.println("wins:"+wins+" ties:"+ties+" losses:"+loss);
		return (float) (hs / count);
	}
}
