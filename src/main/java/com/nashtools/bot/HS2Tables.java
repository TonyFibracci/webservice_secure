package com.nashtools.bot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import com.pokertricks.jni.HandIndexing;


public class HS2Tables {
	
	static{
		byte[] cardsPerRoundFlopImperfectRecall = {2, 3};
		byte[] cardsPerRoundTurnImperfectRecall = {2, 4};
		byte[] cardsPerRoundRiverImperfectRecall = {2, 5};
		imperfectRecallFlopIndexer = HandIndexing.createIndexer(cardsPerRoundFlopImperfectRecall);  
		imperfectRecallTurnIndexer = HandIndexing.createIndexer(cardsPerRoundTurnImperfectRecall);  
		imperfectRecallRiverIndexer = HandIndexing.createIndexer(cardsPerRoundRiverImperfectRecall);  
	}
	
	public static float getFlopHS2(int[] board_cards, int[] hole_cards) {
		byte[] cards = new byte[5];
        cards[0] = (byte)(hole_cards[0]);
        cards[1] = (byte)(hole_cards[1]);
        cards[2] = (byte)(board_cards[0]);
        cards[3] = (byte)(board_cards[1]);
        cards[4] = (byte)(board_cards[2]);
        long[] indexes = new long[2];
        HandIndexing.indexAll(imperfectRecallFlopIndexer, cards, indexes);
        float hs2 = 0;
    	RandomAccessFile file = null;
	    try {
	    	file = new RandomAccessFile(flopHS2Path, "r");
		} 
	    catch (FileNotFoundException e) {
	    	throw new RuntimeException(e);
		}
        FileChannel fileChannel = null;
        fileChannel = file.getChannel();
        MappedByteBuffer buffer = null;
		try {
			buffer = fileChannel.map(MapMode.READ_ONLY, indexes[1] * 4L, 4);
		} catch (IOException e) {
			try {
				file.close();
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		}
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		hs2 = buffer.asFloatBuffer().get();
		try {
			file.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        return hs2;			
	}
	
	public static float getTurnHS2(int[] board_cards, int[] hole_cards) {
		byte[] cards = new byte[6];
        cards[0] = (byte)(hole_cards[0]);
        cards[1] = (byte)(hole_cards[1]);
        cards[2] = (byte)(board_cards[0]);
        cards[3] = (byte)(board_cards[1]);
        cards[4] = (byte)(board_cards[2]);
        cards[5] = (byte)(board_cards[3]);
        long[] indexes = new long[2];
        HandIndexing.indexAll(imperfectRecallTurnIndexer, cards, indexes);
        float hs2 = 0;
    	RandomAccessFile file = null;
	    try {
	    	file = new RandomAccessFile(turnHS2Path, "r");
		} 
	    catch (FileNotFoundException e) {
	    	throw new RuntimeException(e);
		}
        FileChannel fileChannel = null;
        fileChannel = file.getChannel();
        MappedByteBuffer buffer = null;
		try {
			buffer = fileChannel.map(MapMode.READ_ONLY, indexes[1] * 4L, 4);
		} catch (IOException e) {
			try {
				file.close();
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		}
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		hs2 = buffer.asFloatBuffer().get();
		try {
			file.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        return hs2;			
	}
	
	public static float getRiverHS(int[] board_cards, int[] hole_cards) {
		byte[] cards = new byte[7];
        cards[0] = (byte)(hole_cards[0]);
        cards[1] = (byte)(hole_cards[1]);
        cards[2] = (byte)(board_cards[0]);
        cards[3] = (byte)(board_cards[1]);
        cards[4] = (byte)(board_cards[2]);
        cards[5] = (byte)(board_cards[3]);
        cards[6] = (byte)(board_cards[4]);
        long[] indexes = new long[2];
        HandIndexing.indexAll(imperfectRecallRiverIndexer, cards, indexes);
        float hs = 0;
    	RandomAccessFile file = null;
	    try {
	    	file = new RandomAccessFile(riverHS2Path, "r");
		} 
	    catch (FileNotFoundException e) {
	    	throw new RuntimeException(e);
		}
        FileChannel fileChannel = null;
        fileChannel = file.getChannel();
        MappedByteBuffer buffer = null;
		try {
			buffer = fileChannel.map(MapMode.READ_ONLY, indexes[1] * 4L, 4);
		} catch (IOException e) {
			try {
				file.close();
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		}
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		hs = buffer.asFloatBuffer().get();
		try {
			file.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        return hs;			
	}

	static long imperfectRecallFlopIndexer;
	static long imperfectRecallTurnIndexer;
	static long imperfectRecallRiverIndexer;
	static final String flopHS2Path = "H:\\flopIsos.dat";
	static final String turnHS2Path = "H:\\turnIsos.dat";
	static final String riverHS2Path = "H:\\riverIsos.dat";
}
