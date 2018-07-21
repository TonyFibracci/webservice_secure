package com.nashtools.bot.framework;

public class InfoSetNode extends BettingNode{

	public long soln_idx;
	public int num_choices;
	public int player;
	public int round;
	public final BettingNode child;
	
	public InfoSetNode(long new_soln_idx, int new_num_choices, int new_player, int round2, final BettingNode new_child) {
		super();
		this.soln_idx = new_soln_idx;
		this.num_choices = new_num_choices;
		this.player = new_player;
		this.round = round2;
		this.child = new_child;
	}

	@Override
	public int get_num_choices() {
		return num_choices;
	}

	@Override
	public int get_player() {
		return player;
	}

	@Override
	public int get_round() {
		return round;
	}

	@Override
	public long get_soln_idx() {
		return soln_idx;
	}

	@Override
	public BettingNode get_child() {
		return child;
	}
}
