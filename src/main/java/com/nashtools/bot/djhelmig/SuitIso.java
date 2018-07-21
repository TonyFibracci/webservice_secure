package com.nashtools.bot.djhelmig;

public class SuitIso {

	/*
	 * Returns the isomorphic lowest suit pattern.
	 */
	public static int[] lowestSuit(int suits[]) {

		int map[] = new int[] { -1, -1, -1, -1 };
		int isoSuit[] = new int[suits.length];

		int currentsuit = 0;

		for (int i = 0; i < suits.length; i++) {
			if (map[suits[i]] == -1) {
				map[suits[i]] = currentsuit;
				currentsuit++;
			}
			isoSuit[i] = map[suits[i]];
		}
		return isoSuit;
	}

	/*
	 * Returns vector with number of suits of each type.
	 */
	public static int[] suitCount(int suits[]) {

		int count[] = new int[4];

		for (int i = 0; i < suits.length; i++) {
			count[suits[i]]++;
		}

		return count;
	}
}
