package com.nashtools.bot.framework;

public class TerminalNode extends BettingNode{

	boolean showdown;
	int[] fold_value;
	int money;
	
	public TerminalNode(boolean new_showdown, int[] new_fold_value, int new_money) {
		super();
		this.showdown = new_showdown;
		this.money = new_money;
		fold_value = new int[new_fold_value.length];
		fold_value[0] = new_fold_value[0];
		fold_value[1] = new_fold_value[1];
	}

	@Override
	public int get_num_choices() {
		return 0;
	}

	@Override
	public int get_player() {
		return 0;
	}

	@Override
	public int get_round() {
		return 0;
	}

	@Override
	public long get_soln_idx() {
		return 0;
	}

	@Override
	public BettingNode get_child() {
		return null;
	}
	
}
