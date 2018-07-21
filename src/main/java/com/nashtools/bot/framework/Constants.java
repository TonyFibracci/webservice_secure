package com.nashtools.bot.framework;

final public class Constants {
	public final static int MAX_ROUNDS = 4;
	public final static int MAX_PLAYER = 2;
	public final static int MAX_BOARD_CARDS = 7;
	public final static int MAX_HOLE_CARDS = 2;
	public final static int MAX_SUITS = 4;
	public final static int MAX_RANKS = 13;
	public final static int NUM_ACTION_TYPES = 3;
	public final static int MAX_ABSTRACT_ACTIONS = 11;
	public final static int MAX_NUM_ACTIONS = 64;
	
	/* The bytes for storing one entry of the avg-strategy or regrets file used as the strategy.
	 * 4 bytes correspond to an int, 8 bytes correspond to a long.
	 */
	public final static int PREFLOP_BYTE_SIZE = 1;
	public final static int POSTFLOP_BYTE_SIZE = 1;
}
