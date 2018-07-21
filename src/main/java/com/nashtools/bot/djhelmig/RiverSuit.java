package com.nashtools.bot.djhelmig;

import java.util.ArrayList;

public class RiverSuit {

	private int suitMap[][][][][][][] = new int[4][4][4][4][4][4][4];
	private int isoSuitIndex = 0;

	private ArrayList<int[]> patterns = new ArrayList<int[]>();

	RiverSuit() {

		int sizev[] = new int[] { 4, 4, 4, 4, 4, 4, 4 };

		Table.init_int7(suitMap, sizev, -1);

		for (int i = 0; i < sameHand.length; i++)
			sameHand[i] = -1;
	}

	public int getSize() {

		return patterns.size();
	}

	public int[] getPattern(int i) {

		return patterns.get(i);
	}

	public int getPatternIndex(int p[]) {

		return suitMap[p[0]][p[1]][p[2]][p[3]][p[4]][p[5]][p[6]];
	}

	// Suits 0..3, Ranks 0..6, 7 cards, max card index 6*4+3
	// see http://en.wikipedia.org/wiki/Combinadic
	// only needed during creation so can be shared among all RiverSuit:s
	private static int sameHand[] = new int[2 * 98280];

	private int sameHandIndex(int ranks[], int suits[]) {

		int[] cards = Table.sortedIsoBoard(ranks, suits);
		int suited = (suits[0] == suits[1]) ? 1 : 0;

		int hidx = 0;
		for (int i = 0; i < 5; i++) {
			hidx += Table.nchoosek(cards[i], i + 1);
		}

		hidx += suited * 98280;

		return hidx;
	}

	private int sameBoard(int ranks[], int suits[]) {

		int hidx = sameHandIndex(ranks, suits);

		return sameHand[hidx];
	}

	private void addSameBoard(int ranks[], int suits[], int index) {

		int hidx = sameHandIndex(ranks, suits);

		sameHand[hidx] = index;
	}

	// first index to a suit pattern with less than three of same suit
	private int lessthanthreeindex = -1;

	// see if there is less than three of one suit, then all map to same suit
	private boolean lessthanthree(int suits[]) {

		int board[] = new int[] { suits[2], suits[3], suits[4], suits[5], suits[6] };
		int count[] = SuitIso.suitCount(board);

		boolean less = true;

		for (int i = 0; i < 4; i++)
			if (count[i] >= 3)
				less = false;

		return less;
	}

	private int getSuitMapIndex(int s[]) {

		return suitMap[s[0]][s[1]][s[2]][s[3]][s[4]][s[5]][s[6]];
	}

	private void setSuitMapIndex(int s[], int index) {

		suitMap[s[0]][s[1]][s[2]][s[3]][s[4]][s[5]][s[6]] = index;
	}

	private void addSuit(int Rank[], int suits[]) {

		int lowSuits[] = SuitIso.lowestSuit(suits);
		int isuit[] = lowSuits;

		if (!Table.isoHandCheck(Rank, isuit)) {
			// System.out.println("bad card+rank combo");
			setSuitMapIndex(suits, -2);
			return;
		}

		int seenHandIndex = sameBoard(Rank, isuit);
		if (seenHandIndex > -1) {
			setSuitMapIndex(suits, seenHandIndex);
			return;
		}

		if (lessthanthreeindex > -1 && lessthanthree(isuit)) {
			setSuitMapIndex(suits, lessthanthreeindex);
			return;
		}

		int lowSuitIndex = getSuitMapIndex(isuit);
		// we haven't come across this suit pattern yet
		if (lowSuitIndex == -1) {
			setSuitMapIndex(isuit, isoSuitIndex);
			setSuitMapIndex(suits, isoSuitIndex);

			addSameBoard(Rank, isuit, isoSuitIndex);

			if ((lessthanthreeindex < 0) && lessthanthree(isuit))
				lessthanthreeindex = isoSuitIndex;

			patterns.add(isuit);
			isoSuitIndex++;
		} else {
			setSuitMapIndex(suits, lowSuitIndex);
		}
	}

	/*
	 * Enumerate the smallest set of board suit patterns for this board rank
	 * pattern.
	 */
	public void enumSuits(int[] Rank) {

		int suits[] = new int[7];

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				for (int k = 0; k < 4; k++)
					for (int l = 0; l < 4; l++)
						for (int m = 0; m < 4; m++)
							for (int n = 0; n < 4; n++)
								for (int o = 0; o < 4; o++) {
									suits[0] = i;
									suits[1] = j;
									suits[2] = k;
									suits[3] = l;
									suits[4] = m;
									suits[5] = n;
									suits[6] = o;

									addSuit(Rank, suits);
								}
	}
}
