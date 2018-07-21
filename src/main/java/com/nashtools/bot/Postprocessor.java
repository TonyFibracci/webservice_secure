package com.nashtools.bot;

import com.nashtools.bot.framework.Action;
import com.nashtools.bot.framework.ActionType;
import com.nashtools.bot.framework.State;

public class Postprocessor {
	
	private double threshold1 = 0.3;
	private double threshold2 = 0.1;

	public void postprocess(State state, Action[] actions, double[] actionProbs, int numChoices){
		/*if(state.round == 0 && state.numActions[0] == 0){
			actionProbs[num_actions - 1] += actionProbs[num_actions - 2];
			actionProbs[num_actions - 2] = 0;
		}*/
		
		/* Fold biased purify */
	    if(actions[0].type == ActionType.FOLD && actionProbs[0] > threshold1){
	    	actionProbs[0] = 1.0;
	        for(int action = 1; action < numChoices; action++){
	        	actionProbs[action] = 0;
	        }
	    }
	    
	    /* Sum up raises */
		int highestRaiseIdx = 0;
		double highestRaise = 0;
		double sum = 0;
	    for(int action = 1; action < numChoices; action++){
	    	if(actions[action].type == ActionType.RAISE){
	    		if(actionProbs[action] >= highestRaise){
	    			highestRaise = actionProbs[action];
	    			highestRaiseIdx = action;
	    		}
	    		if(actionProbs[action] < threshold2 && actionProbs[action] > 0){
	    			sum += actionProbs[action];
	    			actionProbs[action] = 0; 
	    		}
	    	}
	    }
	    actionProbs[highestRaiseIdx] += sum;
	    
        double counter = 0;
        boolean changed = false;
        for(int action = 0; action < numChoices; action++){
            if(actionProbs[action] < threshold2 && actionProbs[action] > 0){
            	actionProbs[action] = 0;
            	changed = true;
            }
            else{
                counter += actionProbs[action];
            }
        }
    	if(changed){
    		for(int action = 0; action < numChoices; action++){
    			actionProbs[action] = actionProbs[action]/counter;    
    		}	
    	}
	}
}
