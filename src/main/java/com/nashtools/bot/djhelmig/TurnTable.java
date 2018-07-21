package com.nashtools.bot.djhelmig;

public class TurnTable {

	private int count = 0;
	private int dryrun = 1;

	private final int tableSize = 15111642;
	private float LUT[] = new float[tableSize];

	// The different suit patterns matching to each rank pattern. [number of rank
	// patterns]
	private TurnSuit rankPatternSuits[] = new TurnSuit[89];
	// Says to which rank pattern this rank belongs.
	private int[][][][][][] rankPatternIndex = new int[6][6][6][6][6][6];
	// how many ranks that compress into a certain rank pattern [number of rank
	// patterns]
	private int[] numRankPattern = new int[89];
	// Gives each rank a unique index with each rank pattern [number of rank
	// indices]
	private int[] rankPositionMap = new int[165620];
	// Says to which rank pattern index this rank belongs to [number of ranks]
	private int[] rankIndexMap = new int[165620];
	// The current smallest un-used rank pattern index
	private int rankPatternCount = 0;

	// sort hole cards, sort board cards and put into Rank and Suit
	private void getRankSuits(int cards[], int Rank[], int Suit[]) {

		int bmap[] = Table.sortMap(new int[] { cards[2], cards[3], cards[4], cards[5] });
		int hmap[] = (cards[0] < cards[1]) ? new int[] { 0, 1 } : new int[] { 1, 0 };

		for (int i = 0; i < 2; i++) {
			Rank[i] = cards[hmap[i]] / 4;
			Suit[i] = cards[hmap[i]] % 4;
		}

		for (int i = 0; i < 4; i++) {
			Rank[i + 2] = cards[bmap[i] + 2] / 4;
			Suit[i + 2] = cards[bmap[i] + 2] % 4;
		}
	}

	/*
	 * Entry for looking up a hand. First two cards are hole rest board.
	 */
	public float lookupOne(int cards[]) {

		int Rank[] = new int[6];
		int suits[] = new int[6];

		getRankSuits(cards, Rank, suits);

		int rankidx = TurnRank.handRankIndex(Rank);
		int rankIsoIndex = rankIndexMap[rankidx];
		int suitIndex = rankPatternSuits[rankIsoIndex].getPatternIndex(suits);

		int finalindex = tableIndex(Rank, rankIsoIndex, suitIndex);

		return LUT[finalindex];
	}

	private int tableIndex(int[] Rank, int rankIsoIndex, int suitIndex) {

		int rankidx = TurnRank.handRankIndex(Rank);

		int offset = 0;
		for (int i = 0; i < rankIsoIndex; i++) {
			offset += numRankPattern[i] * rankPatternSuits[i].getSize();
		}

		int index = offset + rankPositionMap[rankidx] * rankPatternSuits[rankIsoIndex].getSize() +
				suitIndex;

		return index;
	}

	private TurnEHS tthread[] = new TurnEHS[2];

	private void fillTable(int[] Rank, int suitpattern[], int rankIsoIndex, int suitIndex) {

		int idx = tableIndex(Rank, rankIsoIndex, suitIndex);

		// Generates one non-unique hand
		int cards[] = Table.getHand(Rank, suitpattern);

		// some info of how it's doing since it takes a very long time.
		count++;

		if (count % 50000 == 0) {
			System.out.println("Finished:" + count);
		}

		// so disgusting code
		int ct = count % 2;
		if (tthread[ct] != null) {
			try {
				tthread[ct].join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		tthread[ct] = new TurnEHS();
		tthread[ct].setHand(cards, LUT, idx);
		tthread[ct].start();
	}

	/*
	 * Iterates all suits matching this rank.
	 */
	private void enumerateSuits(int[] Rank) {

		int rankidx = TurnRank.handRankIndex(Rank);

		// index of this rank pattern
		int rankIsoIndex = rankIndexMap[rankidx];

		// These are the suit patterns belonging to this rank pattern
		TurnSuit t = rankPatternSuits[rankIsoIndex];

		// For each suit pattern belonging to this rank pattern
		for (int i = 0; i < t.getSize(); i++) {
			fillTable(Rank, t.getPattern(i), rankIsoIndex, i);
		}
	}

	/*
	 * Only used when doing a dry run to count and generate tables.
	 */
	private void countRankSuits(int[] Rank) {

		int r[] = RankIso.lowestRank(Rank);
		int rankIsoIndex = rankPatternIndex[r[0]][r[1]][r[2]][r[3]][r[4]][r[5]];

		// Haven't come upon this rank pattern yet, add it.
		if (rankIsoIndex == -1) {
			rankPatternSuits[rankPatternCount] = new TurnSuit();
			TurnSuit t = rankPatternSuits[rankPatternCount];
			rankPatternIndex[r[0]][r[1]][r[2]][r[3]][r[4]][r[5]] = rankPatternCount;

			t.enumSuits(r);

			rankIsoIndex = rankPatternCount;
			rankPatternCount++;
		}

		int rankidx = TurnRank.handRankIndex(Rank);

		rankPositionMap[rankidx] = numRankPattern[rankIsoIndex];
		rankIndexMap[rankidx] = rankIsoIndex;

		numRankPattern[rankIsoIndex]++;
	}

	private void enumerateBoard(int[] Rank) {

		for (int i = 0; i < 13; i++)
			for (int j = i; j < 13; j++)
				for (int k = j; k < 13; k++)
					for (int l = k; l < 13; l++) {
						Rank[2] = i;
						Rank[3] = j;
						Rank[4] = k;
						Rank[5] = l;

						// skip 5 of a kind
						if (RankIso.numMaxRanks(Rank) > 4)
							continue;

						if (dryrun == 1)
							countRankSuits(Rank);
						else {
							enumerateSuits(Rank);
						}
					}
	}

	private void enumerateHole() {

		int[] Rank = new int[6];

		for (int i = 0; i < 13; i++) {
			for (int j = i; j < 13; j++) {
				Rank[0] = i;
				Rank[1] = j;

				enumerateBoard(Rank);
			}
		}
	}

	public void initialize() {

		buildTables();
		enumerateHole();

		System.gc();
		debugInfo();

		if (!Table.load("turnehs.dat", LUT)) {
			dryrun = 0;
			enumerateHole();

			// wait for the threads to finish their work (1 can be in progress)
			waitForThreads();

			// now write to disk
			Table.save("turnehs.dat", LUT);
		}
	}

	private void buildTables() {

		int sizev[] = new int[] { 6, 6, 6, 6, 6, 6 };

		// Set all rankPatternIndex entries to -1
		Table.init_int6(rankPatternIndex, sizev, -1);
	}

	private void waitForThreads() {

		for (int i = 0; i < 2; i++) {
			try {
				tthread[i].join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int testLUT(int n) {

		int tested = 0;
		float maxabserr = 0;

		for (int i = 0; i < n; i++) {
			int cards[] = Table.randomHand(6);
			float lookup = lookupOne(cards);

			if (lookup <= 0)
				continue;

			float direct = TurnEHS.turnklaatuEHS(cards);

			if (lookup != direct) {
				float abserr = Math.abs(lookup - direct);

				if (maxabserr < abserr)
					maxabserr = abserr;

				Table.printArray("Bad lookup in the LUT:", cards);
				System.out.println("|x-y|=" + abserr);
			}

			tested++;
		}

		if (maxabserr > 0)
			System.out.println("Maximum error:" + maxabserr);

		return tested;
	}

	public void debugInfo() {

		int s = 0;

		for (int i = 0; i < rankPatternCount; i++) {
			s += numRankPattern[i] * rankPatternSuits[i].getSize();
		}

		System.gc();
		long mem0 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	public void handDebug() {

	}

	public static void main(String[] args) {

		TurnTable t = new TurnTable();

		t.initialize();

		t.debugInfo();
		int tested = t.testLUT(10000);
		System.out.println("tested:" + tested);
	}
}