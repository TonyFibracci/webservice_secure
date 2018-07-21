package com.nashtools.bot.framework;

public class FullActionAbstractionFive extends ActionAbstraction {
	
	public final static double[] raise_betsizes = new double[]{0.25, 0.5, 0.75, 1, 1.5, 2, 3};


	@Override
	public int get_actions(Game game, State state, Action[] actions) {
		  int num_actions = 0;
		  for( int a = 0; a < Constants.NUM_ACTION_TYPES; ++a ) {
		    Action action = new Action();
		    action.type = ActionType.values()[a];
		    action.size = 0;
		    if(action.type == ActionType.RAISE) {
		    	int[] min_raise_size = {0};
		    	int[] max_raise_size = {0};
		    	if(Game.raiseIsValid(game, state, min_raise_size, max_raise_size) != 0){
			/* Check for pot-size raise being valid.  First, get the pot size. */
			int pot = 0;
			for( int p = 0; p < game.numPlayers; ++p ) {
			  pot += state.spent[ p ];
			}
			/* Add amount needed to call.  This gives the size of a pot-sized raise */
			int player = Game.currentPlayer(game, state);
			int amount_to_call = state.maxSpent - state.spent[ player ];
			pot += amount_to_call;
	      /* Raise size is total amount of chips committed over all rounds
	       * after making the raise.
	       */
	      int round = state.round;
	      int num_bets = state.numActions[round];
		  int pre_commitment = (int) (max_raise_size[0] * 0.6);
		  int post_commitment = (int) (max_raise_size[0] * 0.7);
		  if(num_bets<=2 || (num_bets==3 && state.action[round][0].type.ordinal()==1)){
		        for(int i = 0; i < raise_betsizes.length; ++i){
		        	int size = (int)(pot * raise_betsizes[i] + ( state.spent[ player ] + amount_to_call ));
		            if(round==0){
		                if( size < pre_commitment && size  >= min_raise_size[0]) {
		                  actions[ num_actions ] = action.copy();
		                  actions[ num_actions ].size = size ;
		                  ++num_actions;
		                }
		            }
		            else{
		                if( size < post_commitment && size  >= min_raise_size[0]) {
		                  actions[ num_actions ] = action.copy();
		                  actions[ num_actions ].size = size ;
		                  ++num_actions;
		                }
		            }
		        }
		  }
		  else{
		        for(int i = 2; i < raise_betsizes.length; ++i){
		        	int size = (int)(pot * raise_betsizes[i] + ( state.spent[ player ] + amount_to_call ));
		            if(round==0){
		                if( size < pre_commitment && size  >= min_raise_size[0]) {
		                  actions[ num_actions ] = action.copy();
		                  actions[ num_actions ].size = size ;
		                  ++num_actions;
		                }
		            }
		            else{
		                if( size < post_commitment && size  >= min_raise_size[0]) {
		                  actions[ num_actions ] = action.copy();
		                  actions[ num_actions ].size = size ;
		                  ++num_actions;
		                }
		            }
		        }
		  }
	    
	    /* Now add all-in */
	    actions[ num_actions ] = action.copy();
	    actions[ num_actions ].size = max_raise_size[0];
	    ++num_actions;
	      }

	    } else if(Game.isValidAction(game, state, false, action) != 0) {
		      /* Fold and call */
		      actions[ num_actions ] = action.copy();
		      ++num_actions;
		    }
	  }

	  return num_actions;

	}

}
