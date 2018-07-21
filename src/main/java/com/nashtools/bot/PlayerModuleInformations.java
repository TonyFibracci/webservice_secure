package com.nashtools.bot;

import java.util.ArrayList;

import com.nashtools.bot.framework.BettingNode;
import com.nashtools.bot.framework.State;

public class PlayerModuleInformations {
	public PlayerModuleInformations(){
		strings = new ArrayList<String>();
	}
	
	public ArrayList<String> strings;
	public State oldState;
	public BettingNode oldNode;
	public boolean heroAllIn = false;
	public boolean wasAllIn = false;
	public boolean hsAllIn = false;
	public boolean hsCall = false;
}
