package com.nashtools.bot.framework;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Action {

	public ActionType type;
	public int size;
	public boolean wasAllin;
	
	
	public Action(Action a) {
		if(a != null){
			this.size = a.size;
			this.type = a.type;
		}
	}
	
	public Action copy(){
		Action a = new Action();
		a.size = this.size;
		a.type = this.type;
		return a;
	}
	
	public String toString(){
		if(type == null)
			return "";
		if(type == ActionType.FOLD)
			return "f";
		if(type == ActionType.CALL)
			return "c";
		if(type == ActionType.RAISE)
			return "r";
		return "INVALID";
	}
	
	public Action() {
	}
}
