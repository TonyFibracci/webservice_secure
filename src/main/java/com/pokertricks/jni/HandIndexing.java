package com.pokertricks.jni;

/**
 * JNI wrapper for the Hand indexder written by Kevin Waugh
 * http://www.aaai.org/ocs/index.php/WS/AAAIW13/paper/viewFile/7042/6491
 * https://github.com/kdub0/hand-isomorphism
 *
 * Note that with the way JNI works, this file must remain in the same package or the header files would have to be recreated.
 */
public class HandIndexing {
	
	/**
	 * Loads the native shared library. Please note, that the library has to be on the library path
	 * and it has to abide to naming conventions. On Windows this means, it must end on .dll and on Linux
	 * it has to be anmed libhandindex.so
	 * You can set the java library path with "Djava"
	 **/
	static {
			try {
				System.loadLibrary("handindex");
			}catch (UnsatisfiedLinkError e) {
				throw e;		
			}
		}

	/**
	 * Creates an indexer
	 * In the native code the indexer is a pointer, the code is meant for 64 bit systems, therefore the pointer is
	 * represented as a java long.
	 * 
	 * @param cardsPerRound
	 * 
	 * How many cards there are on a round. For a standard perfect recall Hold'em hand this would be {2, 3, 1, 1} for instance:
	 * 2 Holecards, 3 Flopcards, 1 Turncard, 1 riverCard.
	 * Imperfect recall can be achieved by grouping cards of several streets, for instance {2, 5}
	 * could represent a HoleCard | Board combination on the river, where the order of the board cards does not matter,
	 * this would be comparable to the system, that DJHelmig indexers use for the river.
	 * 
	 **/
	public static native long createIndexer(byte[] cardsPerRound);

	/** Frees an indexer. Performs several "free" calls in the native code. **/
	public static native void freeIndexer(long indexer);

	/** 
	 * 
	 * Indexes all streets of the used indexer. This is not more computationally expensive, than just indexing the last Street,
	 * because the indexes of the first streets are needed for indexing the later streets.
	 * 
	 * @param indexer
	 * The used indexer. The java long represents a 64bit pointer in native code.
	 * 
	 * @param cards
	 * The cards to index.
	 * 
	 * @param indexes
	 * The computed indexes will be saved in this variable. Must have as many elements, as there are streets
	 * for the used indexer.
	 */
	public static native void indexAll(long indexer, byte[] cards, long[] indexes);

	/**
	 * 
	 * Gets the number of isomorphic elements on each street for the used indexer.
	 * For instance for an indexer with {2, 3}cards per round, which would represent a standard Hold'em Hand up to the Flop
	 * it would return {169, 1755}
	 * 
	 * @param indexer
	 * The used idexer. The java long represents a 64bit pointer in native code.
	 * 
	 * @param sizes
	 * The sizes will be saved in this variable. Must have as many elements as there are streets for the used indexer.
	 */
	public static native void getSizes(long indexer, long[] sizes);
	
	
	/**
	 * 
	 * Returns a canonic hand for a given index on a street.
	 * 
	 * @param indexer
	 * The used idexer. The java long represents a 64bit pointer in native code.
	 * 
	 * @param round
	 * The round for which we want to unindex a hand.
	 * 
	 * @param index
	 * The index, for which we want to know a canonic hand.
	 * 
	 * @param cards
	 * The canonic hand will be saved in this variable. Must have as many elements, as there are cards on the given street.
	 * 
	 * @return
	 * If the unindexing was succesful.
	 */
	public static native boolean unindex(long indexer, int round, long index, byte[] cards);
}
