package com.nashtools.webservice.resources;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.nashtools.bot.Explorer2;
import com.nashtools.bot.framework.Action;
import com.nashtools.bot.framework.ActionType;
import com.nashtools.bot.framework.State;


@RequestScoped
@Path("strategy")
public class StrategyResource {
	
    @Context
    private SecurityContext securityContext;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public String getStrategy() {
		return "STRATEGY RESOURCE2";
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Action displayState(State state) {
		try {
			Action a = Explorer2.handleGameStateChange(state);
			return a;
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(state);
		}
		Action invalid = new Action();
		invalid.type = ActionType.INVALID;
		return invalid;
	}

}
