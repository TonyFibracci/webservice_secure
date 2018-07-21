package com.nashtools.bot.framework;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class State {
	
	public int maxSpent;
	public int handId;
	public int minNoLimitRaiseTo;
	public int[] spent;
	public Action[][] action;
	public int[][] actingPlayer;
	public int[] numActions;
	public int round;
	public boolean finished;
	public Boolean[] playerFolded;
	public int[] boardCards;
	public int[][] holeCards;
	public float effectiveStackSize;
	
	public State() {
		this.spent = new int[Constants.MAX_PLAYER];
		this.action = new Action[Constants.MAX_ROUNDS][Constants.MAX_NUM_ACTIONS];
		this.actingPlayer = new int[Constants.MAX_ROUNDS][Constants.MAX_NUM_ACTIONS];
		this.numActions = new int[Constants.MAX_ROUNDS];
		this.playerFolded = new Boolean[Constants.MAX_PLAYER];
		this.boardCards = new int[Constants.MAX_BOARD_CARDS];
		this.holeCards = new int[Constants.MAX_PLAYER][Constants.MAX_HOLE_CARDS];
		this.action = new Action[Constants.MAX_ROUNDS][Constants.MAX_NUM_ACTIONS];
	}
	
	public State(State s) {
		this.spent = new int[Constants.MAX_PLAYER];
		this.action = new Action[Constants.MAX_ROUNDS][Constants.MAX_NUM_ACTIONS];
		this.actingPlayer = new int[Constants.MAX_ROUNDS][Constants.MAX_NUM_ACTIONS];
		this.numActions = new int[Constants.MAX_ROUNDS];
		this.playerFolded = new Boolean[Constants.MAX_PLAYER];
		this.boardCards = new int[Constants.MAX_BOARD_CARDS];
		this.holeCards = new int[Constants.MAX_PLAYER][Constants.MAX_HOLE_CARDS];
		this.maxSpent = s.maxSpent;
		this.round = s.round;
		this.finished = s.finished;
		this.minNoLimitRaiseTo = s.minNoLimitRaiseTo;
		for(int i = 0; i < s.spent.length; i++){
			this.spent[i] = s.spent[i];
		}
		for(int i = 0; i < s.numActions.length; i++){
			this.numActions[i] = s.numActions[i];
		}
		for(int i = 0; i < s.playerFolded.length; i++){
			this.playerFolded[i] = s.playerFolded[i];
		}
		for(int i = 0; i < s.boardCards.length; i++){
			this.boardCards[i] = s.boardCards[i];
		}
		for(int i = 0; i < s.holeCards.length; i++){
			for(int j = 0; j < s.holeCards[i].length; j++){
				this.holeCards[i][j] = s.holeCards[i][j];
			}
		}
		for(int i = 0; i < s.action.length; i++){
			for(int j = 0; j < s.action[i].length; j++){
				this.action[i][j] = new Action(s.action[i][j]);
			}
		}
		for(int i = 0; i < s.actingPlayer.length; i++){
			for(int j = 0; j < s.actingPlayer[i].length; j++){
				this.actingPlayer[i][j] = s.actingPlayer[i][j];
			}
		}
	}
	

	public int getMaxSpent() {
		return maxSpent;
	}

	public void setMaxSpent(int maxSpent) {
		this.maxSpent = maxSpent;
	}

	public int getHandId() {
		return handId;
	}

	public void setHandId(int handId) {
		this.handId = handId;
	}

	public int getMinNoLimitRaiseTo() {
		return minNoLimitRaiseTo;
	}

	public void setMinNoLimitRaiseTo(int minNoLimitRaiseTo) {
		this.minNoLimitRaiseTo = minNoLimitRaiseTo;
	}

	public int[] getSpent() {
		return spent;
	}

	public void setSpent(int[] spent) {
		this.spent = spent;
	}

	public Action[][] getAction() {
		return action;
	}

	public void setAction(Action[][] action) {
		this.action = action;
	}

	public int[][] getActingPlayer() {
		return actingPlayer;
	}

	public void setActingPlayer(int[][] actingPlayer) {
		this.actingPlayer = actingPlayer;
	}

	public int[] getNumActions() {
		return numActions;
	}

	public void setNumActions(int[] numActions) {
		this.numActions = numActions;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Boolean[] getPlayerFolded() {
		return playerFolded;
	}

	public void setPlayerFolded(Boolean[] playerFolded) {
		this.playerFolded = playerFolded;
	}

	public int[] getBoardCards() {
		return boardCards;
	}

	public void setBoardCards(int[] boardCards) {
		this.boardCards = boardCards;
	}

	public int[][] getHoleCards() {
		return holeCards;
	}

	public void setHoleCards(int[][] holeCards) {
		this.holeCards = holeCards;
	}

	public float getEffectiveStackSize() {
		return effectiveStackSize;
	}

	public void setEffectiveStackSize(float effectiveStackSize) {
		this.effectiveStackSize = effectiveStackSize;
	}
	

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Street: ").append(round).append("\n");
		if(round > 0) {
			int numCards = round - 1 + 3;
			sb.append("Board: ");
			for(int i = 0; i < numCards; i++)
				sb.append(boardCards[i]).append(" ");
			sb.append("\n");
		}
		sb.append("ActingPlayer: ");
		for(int i = 0; i < numActions[round]; i++)
			sb.append(actingPlayer[round][i]).append(" ");
		sb.append("\n");
		return sb.toString();
	}
	

}
