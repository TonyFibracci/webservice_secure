package com.nashtools.bot.djhelmig;

public class FlopRank {

	public static final int[] bRank1 = { 0, 12, 23, 33, 42, 50, 57, 63, 68, 72, 75, 77, 78 };
	public static final int[] bRank2 = { 0, 78, 144, 199, 244, 280, 308, 329, 344, 354, 360, 363, 364 };

	public static int[] handRank(int cards[]) {

		return new int[] { cards[0] / 4, cards[1] / 4, cards[2] / 4, cards[3] / 4, cards[4] / 4 };
	}

	/*
	 * Rank index of the board [0, 454]
	 */
	public static int boardRankIndex(int bRank[]) {

		return bRank2[bRank[0]] + bRank1[bRank[1]] + bRank[2];
	}

	/*
	 * Creates index for every rank (hole rank, board rank) combination.
	 */
	public static int handRankIndex(int Rank[]) {

		int hRank[] = new int[] { Rank[0], Rank[1] };
		int bRank[] = new int[] { Rank[2], Rank[3], Rank[4] };

		int hridx = RankIso.hRankIndex(hRank);
		int bridx = boardRankIndex(bRank);

		return bridx * 91 + hridx;
	}
}
