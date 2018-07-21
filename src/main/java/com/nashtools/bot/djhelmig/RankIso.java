package com.nashtools.bot.djhelmig;

public class RankIso {

	/*
	 * Returns the number of ranks of the most common rank.
	 */
	public static int numMaxRanks(int ranks[]) {

		int count[] = new int[13];
		int max = 0;

		for (int i = 0; i < ranks.length; i++) {
			count[ranks[i]]++;

			if (count[ranks[i]] > count[max])
				max = ranks[i];

		}
		return count[max];
	}

	/*
	 * When bRank is sorted, returns the isomorphic lowest rank pattern.
	 */
	public static int[] lowestRankSorted(int bRank[]) {

		int r[] = new int[bRank.length];
		r[0] = 0;

		for (int i = 1; i < bRank.length; i++) {
			if (bRank[i - 1] == bRank[i])
				r[i] = r[i - 1];
			else
				r[i] = r[i - 1] + 1;
		}

		return r;
	}

	/*
	 * Returns the isomorphic lowest rank pattern.
	 */
	public static int[] lowestRank(int ranks[]) {

		int map[] = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
		int isoRank[] = new int[ranks.length];

		int currentrank = 0;

		for (int i = 0; i < ranks.length; i++) {
			if (map[ranks[i]] == -1) {
				map[ranks[i]] = currentrank;
				currentrank++;
			}
			isoRank[i] = map[ranks[i]];
		}
		return isoRank;
	}

	/*
	 * 0..90
	 */
	private static final int[][] holeRankIndex = new int[][] {
			{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 },
			{ 1, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 },
			{ 2, 14, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35 },
			{ 3, 15, 26, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45 },
			{ 4, 16, 27, 37, 46, 47, 48, 49, 50, 51, 52, 53, 54 },
			{ 5, 17, 28, 38, 47, 55, 56, 57, 58, 59, 60, 61, 62 },
			{ 6, 18, 29, 39, 48, 56, 63, 64, 65, 66, 67, 68, 69 },
			{ 7, 19, 30, 40, 49, 57, 64, 70, 71, 72, 73, 74, 75 },
			{ 8, 20, 31, 41, 50, 58, 65, 71, 76, 77, 78, 79, 80 },
			{ 9, 21, 32, 42, 51, 59, 66, 72, 77, 81, 82, 83, 84 },
			{ 10, 22, 33, 43, 52, 60, 67, 73, 78, 82, 85, 86, 87 },
			{ 11, 23, 34, 44, 53, 61, 68, 74, 79, 83, 86, 88, 89 },
			{ 12, 24, 35, 45, 54, 62, 69, 75, 80, 84, 87, 89, 90 } };

	/*
	 * Hole rank index.
	 */
	public static int hRankIndex(int hRank[]) {

		return holeRankIndex[hRank[0]][hRank[1]];
	}
}
