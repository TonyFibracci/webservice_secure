package com.nashtools.bot.djhelmig;

public class getEHS {

	private static final int AHEAD = 0, TIED = 1, BEHIND = 2;

	public static void main(String[] args) {

		int h[] = { 12 * 4 + 0, 9 * 4 + 1, 4 * 4 + 0, 6 * 4 + 0, 10 * 4 + 1, 11 * 4 + 2 };

		float f = TurnEHS.turnklaatuEHS(h);

		System.out.println(f);
	}

	public static void debugEHS(int HS[], int HP[][], int HPtotal[]) {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.print(HP[i][j] + "\t");
			}
			System.out.println();
		}
		for (int i = 0; i < 3; i++) {
			System.out.print(HPtotal[i] + "\t");
		}
		/*
		 * System.out.println("");
		 * System.out.println("hs" + handstren);
		 * System.out.println("ppot" + ppot);
		 * System.out.println("npot" + npot);
		 */
	}

	public static float calculateEHS(int HS[], int HP[][]) {

		int HPtotal[] = new int[3];

		/*
		 * Sum over HP
		 */
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				HPtotal[i] += HP[i][j];
			}
		}

		float handstren = HS[AHEAD] + HS[TIED] / 2;
		handstren /= (HS[AHEAD] + HS[TIED] + HS[BEHIND]);

		// System.out.println("handstren:" + handstren);

		float ppot = 1;
		float ppotnormalize = HPtotal[BEHIND] + HPtotal[TIED];
		if (ppotnormalize > 0) {
			ppot = (HP[BEHIND][AHEAD] + HP[BEHIND][TIED] / 2 + HP[TIED][AHEAD] / 2) / ppotnormalize;
		}

		// System.out.println("ppot:" + ppot);

		float npot = 1;
		float npotnormalize = (HPtotal[AHEAD] + HPtotal[TIED]);
		if (npotnormalize > 0) {
			npot = (HP[AHEAD][BEHIND] + HP[TIED][BEHIND] / 2 + HP[AHEAD][TIED] / 2) / npotnormalize;
		}

		// System.out.println("npot:" + npot);
		// debugEHS(HS, HP, HPtotal);

		return handstren + (1 - handstren) * ppot - handstren * npot;
	}
}
