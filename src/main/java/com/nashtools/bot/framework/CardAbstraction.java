package com.nashtools.bot.framework;

public abstract class CardAbstraction {
	
	abstract protected int get_bucket(final Game game, final BettingNode node, int[] board_cards, int[][] hole_cards);
	abstract protected int num_buckets(final Game game, final BettingNode node);
	abstract protected int num_bucket(final Game game, final State state);

}
