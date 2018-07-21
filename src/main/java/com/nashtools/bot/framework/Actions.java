package com.nashtools.bot.framework;

public enum Actions {
	FOLD, CALL, RAISE, INVALID;
	
	public String toString(){
		if(this.equals(FOLD))
			return "f";
		if(this.equals(CALL))
			return "c";
		if(this.equals(RAISE))
			return "r";
		return "INVALID";
	}
}
