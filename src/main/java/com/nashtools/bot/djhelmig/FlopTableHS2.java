package com.nashtools.bot.djhelmig;

public class FlopTableHS2 {

	private int count = 0;
	private int dryrun = 1;

	private final int tableSize = 1361802;
	private float LUT[] = new float[tableSize];
	public static boolean initialized = false;

	// The different suit patterns matching to each rank pattern. [number of rank
	// patterns]
	private FlopSuit rankPatternSuits[] = new FlopSuit[36];
	// Says to which rank pattern this rank belongs.
	private int[][][][][] rankPatternIndex = new int[5][5][5][5][5];
	// how many ranks that compress into a certain rank pattern [number of rank
	// patterns]
	private int[] numRankPattern = new int[36];
	// Gives each rank a unique position within the rank pattern it belongs to
	// [number of ranks]
	private int[] rankPositionMap = new int[41405];
	// Says to which rank pattern index this rank belongs to [number of ranks]
	private int[] rankIndexMap = new int[41405];
	// The current smallest un-used rank pattern index
	private int rankPatternCount = 0;

	// sort hole cards, sort board cards and put into Rank and Suit
	private void getRankSuits(int cards[], int Rank[], int Suit[]) {

		int bmap[] = Table.sortMap(new int[] { cards[2], cards[3], cards[4] });
		int hmap[] = (cards[0] < cards[1]) ? new int[] { 0, 1 } : new int[] { 1, 0 };

		for (int i = 0; i < 2; i++) {
			Rank[i] = cards[hmap[i]] / 4;
			Suit[i] = cards[hmap[i]] % 4;
		}

		for (int i = 0; i < 3; i++) {
			Rank[i + 2] = cards[bmap[i] + 2] / 4;
			Suit[i + 2] = cards[bmap[i] + 2] % 4;
		}
	}

	/*
	 * Entry for looking up a hand. First two cards are hole rest board.
	 */
	public float lookupOne(int cards[]) {

		int Rank[] = new int[5];
		int suits[] = new int[5];

		getRankSuits(cards, Rank, suits);
		// Table.printArray("Rank", Rank);
		// Table.printArray("Suit", suits);

		int rankidx = FlopRank.handRankIndex(Rank);
		int rankIsoIndex = rankIndexMap[rankidx];
		int suitIndex = rankPatternSuits[rankIsoIndex].getPatternIndex(suits);

		int suitPattern[] = rankPatternSuits[rankIsoIndex].getPattern(suitIndex);

		int finalindex = tableIndex(Rank, rankIsoIndex, suitIndex);

		return LUT[finalindex];
	}

	private int tableIndex(int[] Rank, int rankIsoIndex, int suitIndex) {

		int rankidx = FlopRank.handRankIndex(Rank);

		int offset = 0;
		// XX: replace with pre-calculated
		for (int i = 0; i < rankIsoIndex; i++) {
			offset += numRankPattern[i] * rankPatternSuits[i].getSize();
		}

		int index = offset + rankPositionMap[rankidx] * rankPatternSuits[rankIsoIndex].getSize() +
				suitIndex;

		return index;
	}

	private FlopHS2 fthread[] = new FlopHS2[2];

	private void fillTable(int[] Rank, int suitpattern[], int rankIsoIndex, int suitIndex) {

		int idx = tableIndex(Rank, rankIsoIndex, suitIndex);

		// Generates one hand
		int cards[] = Table.getHand(Rank, suitpattern);

		// some info of how it's doing since it takes a very long time.
		count++;

		// so disgusting code
		int ct = count % 2;
		if (fthread[ct] != null) {
			try {
				fthread[ct].join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		fthread[ct] = new FlopHS2();
		fthread[ct].setHand(cards, LUT, idx);
		fthread[ct].start();
	}

	private void enumerateSuits(int[] Rank) {

		int rankidx = FlopRank.handRankIndex(Rank);

		// index of this rank pattern
		int rankIsoIndex = rankIndexMap[rankidx];

		// These are the suit patterns belonging to this rank pattern
		FlopSuit f = rankPatternSuits[rankIsoIndex];

		// For each suit pattern belonging to this rank pattern
		for (int i = 0; i < f.getSize(); i++) {
			fillTable(Rank, f.getPattern(i), rankIsoIndex, i);
		}
	}

	/*
	 * Only used when doing a dry run to count and generate indexing tables.
	 */
	private void countRankSuits(int[] Rank) {

		int r[] = RankIso.lowestRank(Rank);
		int rankIsoIndex = rankPatternIndex[r[0]][r[1]][r[2]][r[3]][r[4]];

		// Haven't come upon this rank pattern yet, add it.
		if (rankIsoIndex == -1) {
			rankPatternSuits[rankPatternCount] = new FlopSuit();
			FlopSuit f = rankPatternSuits[rankPatternCount];
			rankPatternIndex[r[0]][r[1]][r[2]][r[3]][r[4]] = rankPatternCount;

			f.enumSuits(r);


			rankIsoIndex = rankPatternCount;
			rankPatternCount++;
		}

		int rankidx = FlopRank.handRankIndex(Rank);

		rankPositionMap[rankidx] = numRankPattern[rankIsoIndex];
		rankIndexMap[rankidx] = rankIsoIndex;

		numRankPattern[rankIsoIndex]++;
	}

	/*
	 * These are strictly for testing but very useful.
	 */
	private final int magicRankNumber = 480;
	private int ranksEnumerated = 0;

	private void enumerateBoard(int[] Rank) {

		for (int k = 0; k < 13; k++) {
			for (int l = k; l < 13; l++) {
				for (int m = l; m < 13; m++) {
					Rank[2] = k;
					Rank[3] = l;
					Rank[4] = m;

					// no 5 of a kind please
					if (Rank[0] == Rank[1] && Rank[1] == Rank[2] && Rank[2] == Rank[4])
						continue;

					if (dryrun == 1)
						countRankSuits(Rank);
					else {
						enumerateSuits(Rank);
						ranksEnumerated++;
					}
				}
			}
		}
	}

	private void enumerateHole() {

		int[] Rank = new int[5];

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

		if (!Table.load("flophs2.dat", LUT)) {
			dryrun = 0;
			enumerateHole();

			// wait for the threads to finish their work (1 can be in progress)
			waitForThreads();

			// now write to disk
			Table.save("flophs2.dat", LUT);
		}
		initialized = true;
		System.gc();
	}

	public void rankSuitsDebug() {

		int r[] = new int[] { 0, 1, 2, 3, 4 };

		int rankidx = FlopRank.handRankIndex(r);
		int rankIsoIndex = rankIndexMap[rankidx];

		FlopSuit f = rankPatternSuits[rankIsoIndex];

		for (int i = 0; i < f.getSize(); i++) {
			int suits[] = f.getPattern(i);
			int cards[] = Table.getHand(r, suits);
			float hs2 = FlopHS2.flopklaatuHS2(cards);
		}
	}

	public void handDebug() {

		int h[] = { 0 * 4 + 0, 1 * 4 + 0, 3 * 4 + 1, 4 * 4 + 1, 7 * 4 + 2 };
		int h2[] = { 0 * 4 + 0, 1 * 4 + 1, 3 * 4 + 2, 4 * 4 + 2, 7 * 4 + 3 };

		int h3[] = { 0 * 4 + 0, 1 * 4 + 1, 3 * 4 + 0, 4 * 4 + 2, 7 * 4 + 3 };
		int h4[] = { 0 * 4 + 0, 1 * 4 + 1, 3 * 4 + 1, 4 * 4 + 2, 7 * 4 + 3 };

		float f = FlopHS2.flopklaatuHS2(h);
		float f2 = FlopHS2.flopklaatuHS2(h2);
		float f3 = FlopHS2.flopklaatuHS2(h3);
		float f4 = FlopHS2.flopklaatuHS2(h4);

		System.out.println(f + " vs " + f2 + " vs " + f3 + " vs " + f4);
	}

	private void waitForThreads() {

		for (int i = 0; i < 2; i++) {
			try {
				fthread[i].join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void buildTables() {

		int sizev[] = new int[] { 5, 5, 5, 5, 5 };

		// Set all rankPatternIndex entries to -1
		Table.init_int5(rankPatternIndex, sizev, -1);
	}

	public int testLUT(int n) {

		int tested = 0;

		for (int i = 0; i < n; i++) {
			int cards[] = Table.randomHand(5);
			float lookup = lookupOne(cards);

			if (lookup <= 0)
				continue;

			float direct = FlopHS2.flopklaatuHS2(cards);

			if (lookup != direct) {
				Table.printArray("Bad lookup in the LUT:", cards);
				System.out.println("direct:" + direct + " and lookupOne:" + lookup);
			}
			tested++;
		}
		return tested;
	}

	public void debugInfo() {

		int s = 0;

		for (int i = 0; i < rankPatternCount; i++) {
			s += numRankPattern[i] * rankPatternSuits[i].getSize();
		}

		long mem0 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	public static void main(String[] args) {

		FlopTableHS2 f = new FlopTableHS2();

		f.initialize();
		// f.handDebug();
		// f.rankSuitsDebug();

		int ntested = f.testLUT(5000);
		System.out.println("Tested " + ntested + " hands");
		// f.handDebug();
	}
}