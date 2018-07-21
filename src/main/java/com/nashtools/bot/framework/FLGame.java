package com.nashtools.bot.framework;

public class FLGame extends Game{

	public FLGame() {
		this.bettingType = BettingType.LIMITBETTING;
		this.numPlayers = 2;
		this.numRounds = 4;
		this.numSuits = 4;
		this.numRanks = Constants.MAX_RANKS;
		this.numHoleCards = 2;
		this.blind = new int[2];
		blind[0] = 10;
		blind[1] = 5;
		this.raiseSize = new int[4];
		raiseSize[0] = 10;
		raiseSize[1] = 10;
		raiseSize[2] = 20;
		raiseSize[3] = 20;
		this.maxRaises = new int[4];
		maxRaises[0] = 3;
		maxRaises[1] = 4;
		maxRaises[2] = 4;
		maxRaises[3] = 4;
		this.firstPlayer = new int[4];
		firstPlayer[0] = 1;
		firstPlayer[1] = 0;
		firstPlayer[2] = 0;
		firstPlayer[3] = 0;
		this.numBoardCards = new int[4];
		numBoardCards[0] = 0;
		numBoardCards[1] = 3;
		numBoardCards[2] = 1;
		numBoardCards[3] = 1;
		this.stack = new int[4];
		for (int i = 0; i < stack.length; i++)
			stack[i] = Integer.MAX_VALUE;
	}
}
