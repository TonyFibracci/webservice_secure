package com.nashtools.bot;

import com.nashtools.bot.framework.Action;
import com.nashtools.bot.framework.ActionType;
import com.nashtools.bot.framework.CapGame;
import com.nashtools.bot.framework.FullActionAbstractionFive;
import com.nashtools.bot.framework.FullActionAbstractionFour;
import com.nashtools.bot.framework.FullActionAbstractionThree;
import com.nashtools.bot.framework.HUSNGGame;
import com.nashtools.bot.framework.Player_Module;
import com.nashtools.bot.framework.State;

public class Explorer2 {
	
	public static Player_Module sng1_5Bot = new Player_Module("H:\\SNG\\strategy1.5.dat", 
			new HUSNGGame(15), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng2Bot = new Player_Module("H:\\SNG\\strategy2.dat", 
			new HUSNGGame(20), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng2_5Bot = new Player_Module("H:\\SNG\\strategy2.5.dat", 
			new HUSNGGame(25), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng3Bot = new Player_Module("H:\\SNG\\strategy3.dat", 
			new HUSNGGame(30), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng3_5Bot = new Player_Module("H:\\SNG\\strategy3.5.dat", 
			new HUSNGGame(35), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng4Bot = new Player_Module("H:\\SNG\\strategy4.dat", 
			new HUSNGGame(40), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng4_5Bot = new Player_Module("H:\\SNG\\strategy4.5.dat", 
			new HUSNGGame(45), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng5Bot = new Player_Module("H:\\SNG\\strategy5.dat", 
			new HUSNGGame(50), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng5_5Bot = new Player_Module("H:\\SNG\\strategy5.5.dat", 
			new HUSNGGame(55), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng6Bot = new Player_Module("H:\\SNG\\strategy6.dat", 
			new HUSNGGame(60), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng6_5Bot = new Player_Module("H:\\SNG\\strategy6.5.dat", 
			new HUSNGGame(65), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng7Bot = new Player_Module("H:\\SNG\\strategy7.dat", 
			new HUSNGGame(70), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng7_5Bot = new Player_Module("H:\\SNG\\strategy7.5.dat", 
			new HUSNGGame(75), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng8Bot = new Player_Module("H:\\SNG\\strategy8.dat", 
			new HUSNGGame(80), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng8_5Bot = new Player_Module("H:\\SNG\\strategy8.5.dat", 
			new HUSNGGame(85), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng9Bot = new Player_Module("H:\\SNG\\strategy9.dat", 
			new HUSNGGame(90), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng9_5Bot = new Player_Module("H:\\SNG\\strategy9.5_2.dat", 
			new HUSNGGame(95), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng10Bot = new Player_Module("H:\\SNG\\strategy10.dat", 
			new HUSNGGame(100), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng10_5Bot = new Player_Module("H:\\SNG\\strategy10.5_2.dat", 
			new HUSNGGame(105), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng11Bot = new Player_Module("H:\\SNG\\strategy11.dat", 
			new HUSNGGame(110), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng11_5Bot = new Player_Module("H:\\SNG\\strategy11.5.dat", 
			new HUSNGGame(115), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng12Bot = new Player_Module("H:\\SNG\\strategy12_2.dat", 
			new HUSNGGame(120), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng13Bot = new Player_Module("H:\\SNG\\strategy13_2.dat", 
			new HUSNGGame(130), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng14Bot = new Player_Module("H:\\SNG\\strategy14.dat", 
			new HUSNGGame(140), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng15Bot = new Player_Module("H:\\SNG\\strategy15_2.dat", 
			new HUSNGGame(150), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng16Bot = new Player_Module("H:\\SNG\\strategy16_2.dat", 
			new HUSNGGame(160), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng17Bot = new Player_Module("H:\\SNG\\strategy17_2.dat", 
			new HUSNGGame(170), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng18Bot = new Player_Module("H:\\SNG\\strategy18_2.dat", 
			new HUSNGGame(180), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng19Bot = new Player_Module("H:\\SNG\\strategy19.dat", 
			new HUSNGGame(190), new OpenPureCFRCardAbstractionCP(), new FullActionAbstractionFour(), new Postprocessor());
	public static Player_Module capBot = new Player_Module("H:\\cap1009.dat", 
			new CapGame(), new OpenPureCFRCardAbstractionCap(), new FullActionAbstractionThree(), new Postprocessor());
	public static Player_Module sng21Bot = new Player_Module("H:\\SNG\\strategy21.dat", 
			new HUSNGGame(210), new OpenPureCFRCardAbstractionCP(), new FullActionAbstractionFive(), new Postprocessor());
	public static Player_Module sng22Bot = new Player_Module("H:\\SNG\\strategy22.dat", 
			new HUSNGGame(220), new OpenPureCFRCardAbstractionCP(), new FullActionAbstractionFive(), new Postprocessor());
	public static Player_Module sng23Bot = new Player_Module("H:\\SNG\\strategy23.dat", 
			new HUSNGGame(230), new OpenPureCFRCardAbstractionCP(), new FullActionAbstractionFive(), new Postprocessor());
	public static Player_Module sng24Bot = new Player_Module("H:\\SNG\\strategy24.dat", 
			new HUSNGGame(240), new OpenPureCFRCardAbstractionCP(), new FullActionAbstractionFive(), new Postprocessor());
	public static Player_Module sng25Bot = new Player_Module("H:\\SNG\\strategy25.dat", 
			new HUSNGGame(250), new OpenPureCFRCardAbstractionCP(), new FullActionAbstractionFive(), new Postprocessor());
	
	public static Action handleGameStateChange(State state) {
		Action a = null;
		if(state.effectiveStackSize < 1.5){
			a = new Action();
			a.type = ActionType.CALL;
			a.size = 0;
		}
		if(state.effectiveStackSize == 1.5){
			a = sng1_5Bot.get_action(state);
		}
		if(state.effectiveStackSize == 2){
			a = sng2Bot.get_action(state);
		}
		if(state.effectiveStackSize == 2.5){
			a = sng2_5Bot.get_action(state);
		}
		if(state.effectiveStackSize == 3){
			a = sng3Bot.get_action(state);
		}
		if(state.effectiveStackSize == 3.5){
			a = sng3_5Bot.get_action(state);
		}
		if(state.effectiveStackSize == 4){
			a = sng4Bot.get_action(state);
		}
		if(state.effectiveStackSize == 4.5){
			a = sng4_5Bot.get_action(state);
		}
		if(state.effectiveStackSize == 5){
			a = sng5Bot.get_action(state);
		}
		if(state.effectiveStackSize == 5.5){
			a = sng5_5Bot.get_action(state);
		}
		if(state.effectiveStackSize == 6){
			a = sng6Bot.get_action(state);
		}
		if(state.effectiveStackSize == 6.5){
			a = sng6_5Bot.get_action(state);
		}
		if(state.effectiveStackSize == 7){
			a = sng7Bot.get_action(state);
		}
		if(state.effectiveStackSize == 7.5){
			a = sng7_5Bot.get_action(state);
		}
		if(state.effectiveStackSize == 8){
			a = sng8Bot.get_action(state);
		}
		if(state.effectiveStackSize == 8.5){
			a = sng8_5Bot.get_action(state);
		}
		if(state.effectiveStackSize == 9){
			a = sng9Bot.get_action(state);
		}
		if(state.effectiveStackSize == 9.5){
			a = sng9_5Bot.get_action(state);
		}
		if(state.effectiveStackSize == 10){
			a = sng10Bot.get_action(state);
		}
		if(state.effectiveStackSize == 10.5){
			a = sng10_5Bot.get_action(state);
		}
		if(state.effectiveStackSize == 11){
			a = sng11Bot.get_action(state);
		}
		if(state.effectiveStackSize == 11.5){
			a = sng11_5Bot.get_action(state);
		}
		if(state.effectiveStackSize == 12){
			a = sng12Bot.get_action(state);
		}
		if(state.effectiveStackSize == 13){
			a = sng13Bot.get_action(state);
		}
		if(state.effectiveStackSize == 14){
			a = sng14Bot.get_action(state);
		}
		if(state.effectiveStackSize == 15){
			a = sng15Bot.get_action(state);
		}
		if(state.effectiveStackSize == 16){
			a = sng16Bot.get_action(state);
		}
		if(state.effectiveStackSize == 17){
			a = sng17Bot.get_action(state);
		}
		if(state.effectiveStackSize == 18){
			a = sng18Bot.get_action(state);
		}
		if(state.effectiveStackSize == 19){
			a = sng19Bot.get_action(state);
		}
		if(state.effectiveStackSize == 20){
			a = capBot.get_action(state);
		}
		if(state.effectiveStackSize == 21){
			a = sng21Bot.get_action(state);
		}
		if(state.effectiveStackSize == 22){
			a = sng22Bot.get_action(state);
		}
		if(state.effectiveStackSize == 23){
			a = sng23Bot.get_action(state);
		}
		if(state.effectiveStackSize == 24){
			a = sng24Bot.get_action(state);
		}		
		if(state.effectiveStackSize == 25){
			a = sng25Bot.get_action(state);
		}	
		return a;
	}

}
