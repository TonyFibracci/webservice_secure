package com.nashtools.bot.djhelmig;

public class TurnRank {

	private static final int[] n = { 0, 12, 23, 33, 42, 50, 57, 63, 68, 72, 75, 77, 78 };
	private static final int[] m = { 0, 78, 144, 199, 244, 280, 308, 329, 344, 354, 360, 363, 364 };
	private static final int[] o = { 0, 364, 650, 870, 1035, 1155, 1239, 1295, 1330, 1350, 1360,
			1364, 1365 };

	/*
	 * Rank index of the board [0, 1820-1]
	 */
	public static int boardRankIndex(int bRank[]) {

		return o[bRank[0]] + m[bRank[1]] + n[bRank[2]] + bRank[3];
	}

	/*
	 * Creates a unique index for every rank (hole rank, board rank) combination.
	 */
	public static int handRankIndex(int Rank[]) {

		int hRank[] = new int[] { Rank[0], Rank[1] };
		int bRank[] = new int[] { Rank[2], Rank[3], Rank[4], Rank[5] };

		int hridx = RankIso.hRankIndex(hRank);
		int bridx = boardRankIndex(bRank);

		return bridx * 91 + hridx;
	}
}
