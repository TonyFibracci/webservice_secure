package com.nashtools.bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.nashtools.bot.framework.BettingNode;
import com.nashtools.bot.framework.CardAbstraction;
import com.nashtools.bot.framework.Game;
import com.nashtools.bot.framework.State;
import com.pokertricks.jni.HandIndexing;



/**
 * 
 * Calculates buckets for a hand, this can either be an abstracted bucket, or an isomorphic index.
 * All Lookup tables have to be raw integer arrays.
 *
 */
public class OpenPureCFRCardAbstractionCP extends CardAbstraction{
	
  /* Numbers of buckets, needed to create the entries */
  static final int numberOfPreflopBuckets = 169;
  static final int numFlopBuckets = 1286792;
  static final int numTurnBuckets = 59995;
  static final int numRiverBuckets = 180000;
  
  /* Wether we use bucketing on the flop or only use isomorphisms */
  static final boolean unabstractedFlop = true;
  
  /* The indexers for each street, need not to be changed for different abstractions */
  static long preflopIndexer;
  static long flopIndexer;
  static long imperfectRecallTurnIndexer;
  static long perfectRecallTurnIndexer;
  static long imperfectRecallRiverIndexer;
  static long perfectRecallRiverIndexer;
  static{
		byte[] cardsPerRoundTurnPerfectRecall = {2, 3, 1};
		byte[] cardsPerRoundRiverPerfectRecall = {2, 3, 1, 1};
		byte[] cardsPerRoundPreflopImperfectRecall = {2};
		byte[] cardsPerRoundFlopImperfectRecall = {2, 3};
		byte[] cardsPerRoundTurnImperfectRecall = {2, 4};
		byte[] cardsPerRoundRiverImperfectRecall = {2, 5};
		perfectRecallRiverIndexer = HandIndexing.createIndexer(cardsPerRoundRiverPerfectRecall);  
		perfectRecallTurnIndexer = HandIndexing.createIndexer(cardsPerRoundTurnPerfectRecall);  
		preflopIndexer = HandIndexing.createIndexer(cardsPerRoundPreflopImperfectRecall);  
		flopIndexer = HandIndexing.createIndexer(cardsPerRoundFlopImperfectRecall);  
		imperfectRecallTurnIndexer = HandIndexing.createIndexer(cardsPerRoundTurnImperfectRecall);  
		imperfectRecallRiverIndexer = HandIndexing.createIndexer(cardsPerRoundRiverImperfectRecall);  
  }
  
  /* Wether we use perfect or imperfect recall bucketing */
  static final boolean perfectRecallTables = true;
  
  /* Paths to the LUTs for the bucketing */
  static final String flopLUTPath = "";
  static final String turnLUTPath = "H:\\cpbuckets\\turnBuckets.dat";
  static final String riverLUTPath = "H:\\cpbuckets\\riverBuckets.dat";
  
  /* Number of isomorhic indexes on each street */
  static final int numPreflopIndexes = 169;
  static final int numFlopIndexes = 1286792;
  static final int numTurnIndexesPerfectRecall = 55190538;
  static final int numTurnIndexesImperfectRecall = 13960050;
  
  /* The table for the flopbuckets. Due to the small flop size, this is the only table we load into RAM */
  static int[] flopTable;
  static{
	  if(!unabstractedFlop){
		  flopTable = new int[numFlopIndexes];
	        File file = new File(flopLUTPath);
	        FileChannel fileChannel = null;
	        try {
				fileChannel = new FileInputStream(file).getChannel();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
	        long length = 4 * numFlopIndexes;
	        MappedByteBuffer buffer = null;
	        try {
				buffer = fileChannel.map(MapMode.READ_ONLY, 0, length);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	        buffer.order(ByteOrder.LITTLE_ENDIAN);
	        buffer.asIntBuffer().get(flopTable);		 
	  } 
  }
  

	public OpenPureCFRCardAbstractionCP() {

	}
	
	@Override
	public int get_bucket(Game game, BettingNode node, int[] board_cards, int[][] hole_cards) {
		 switch(node.get_round())
		    {
		    /* Preflop is always unabstracted */
		    case 0:
		    {
		    	byte[] cards = new byte[2];
		        cards[0] = (byte)(hole_cards[node.get_player()][0]);
		        cards[1] = (byte)((hole_cards[node.get_player()][1]));
		        long[] indexes = new long[1];
		        HandIndexing.indexAll(preflopIndexer, cards, indexes);
		        return (int)indexes[0];
		    }
		    /* Return the index for an unabstracted hand or look up the bucket from a table */
		    case 1:
		    {
		    	byte[] cards = new byte[5];
		        cards[0] = (byte)(hole_cards[node.get_player()][0]);
		        cards[1] = (byte)((hole_cards[node.get_player()][1]));
		        cards[2] = (byte)(board_cards[0]);
		        cards[3] = (byte)(board_cards[1]);
		        cards[4] = (byte)(board_cards[2]);
		        long[] indexes = new long[2];
		        HandIndexing.indexAll(flopIndexer, cards, indexes);
				//System.out.println(indexes[1]);
		        if(unabstractedFlop)
		        	return (int)indexes[1];
		        else
		        	return flopTable[(int)indexes[1]];
		    }
		    /* Turn is never unabstracted, lookup the table from the HDD */
		    case 2:
		    {
		    	byte[] cards = new byte[6];
		        cards[0] = (byte)(hole_cards[node.get_player()][0]);
		        cards[1] = (byte)((hole_cards[node.get_player()][1]));
		        cards[2] = (byte)(board_cards[0]);
		        cards[3] = (byte)(board_cards[1]);
		        cards[4] = (byte)(board_cards[2]);
		        cards[5] = (byte)(board_cards[3]);
		        long[] indexes = new long[3];
		        if(perfectRecallTables)
		        	HandIndexing.indexAll(perfectRecallTurnIndexer, cards, indexes);
		        else
		        	HandIndexing.indexAll(imperfectRecallTurnIndexer, cards, indexes);
		        int bucket = 0;
	        	RandomAccessFile file = null;
			    try {
			    	file = new RandomAccessFile(turnLUTPath, "r");
				} 
			    catch (FileNotFoundException e) {
			    	throw new RuntimeException(e);
				}
		        FileChannel fileChannel = null;
		        fileChannel = file.getChannel();
		        MappedByteBuffer buffer = null;
				try {
					buffer = fileChannel.map(MapMode.READ_ONLY, indexes[2] * 4L, 4);
				} catch (IOException e) {
					try {
						file.close();
					} catch (IOException e1) {
						throw new RuntimeException(e1);
					}
					throw new RuntimeException(e);
				}
				buffer.order(ByteOrder.LITTLE_ENDIAN);
				bucket = buffer.asIntBuffer().get();
				try {
					file.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				//System.out.println(bucket);
		        return bucket;
		    }
		    /* River is never unabstracted, lookup the table from the HDD */
		    case 3:
		    {
		    	byte[] cards = new byte[7];
		        cards[0] = (byte)(hole_cards[node.get_player()][0]);
		        cards[1] = (byte)((hole_cards[node.get_player()][1]));
		        cards[2] = (byte)(board_cards[0]);
		        cards[3] = (byte)(board_cards[1]);
		        cards[4] = (byte)(board_cards[2]);
		        cards[5] = (byte)(board_cards[3]);
		        cards[6] = (byte)(board_cards[4]);
		        long[] indexes = new long[4];
		        if(perfectRecallTables)
		        	HandIndexing.indexAll(perfectRecallRiverIndexer, cards, indexes);
		        else
		        	HandIndexing.indexAll(imperfectRecallRiverIndexer, cards, indexes);
		        int bucket = 0;
	        	RandomAccessFile file = null;
			    try {
			    	file = new RandomAccessFile(riverLUTPath, "r");
				} 
			    catch (FileNotFoundException e) {
			    	throw new RuntimeException(e);
				}
		        FileChannel fileChannel = null;
		        fileChannel = file.getChannel();
		        MappedByteBuffer buffer = null;
				try {
					buffer = fileChannel.map(MapMode.READ_ONLY, indexes[3] * 4L, 4);
				} catch (IOException e) {
					try {
						file.close();
					} catch (IOException e1) {
						throw new RuntimeException(e1);
					}
					throw new RuntimeException(e);
				}
				buffer.order(ByteOrder.LITTLE_ENDIAN);
				bucket = buffer.asIntBuffer().get();
				try {
					file.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				//System.out.println(bucket);
		        return bucket;		 
		    }
		 }
		 throw new RuntimeException();
	}

	@Override
	public int num_buckets(Game game, BettingNode node) {
	    switch(node.get_round()){
	    case 0:
	        return numberOfPreflopBuckets;
	    case 1:
	        return numFlopBuckets;
	    case 2:
	        return numTurnBuckets;
	    case 3:
	        return numRiverBuckets;
	    }
	    throw new RuntimeException("Invalid Street");
	}
	

	@Override
	public int num_bucket(Game game, State state) {
	    switch(state.round){
	    case 0:
	        return numberOfPreflopBuckets;
	    case 1:
	        return numFlopBuckets;
	    case 2:
	        return numTurnBuckets;
	    case 3:
	        return numRiverBuckets;
	    }
	    throw new RuntimeException("Invalid Street");
	}
}