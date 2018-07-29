package com.cassiomolin.example.user.api.resource;

import com.cassiomolin.example.user.api.model.QueryUserResult;
import com.cassiomolin.example.user.domain.UserAccount;
import com.cassiomolin.example.user.service.UserService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JAX-RS resource class that provides operations for users.
 *
 * @author cassiomolin
 */
@RequestScoped
@Path("users")
public class UserResource {

    @Context
    private UriInfo uriInfo;

    @Context
    private SecurityContext securityContext;

    @Inject
    private UserService userService;
   

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response getUsers() {

        List<QueryUserResult> queryUserResults = userService.findAll().stream()
                .map(this::toQueryUserResult)
                .collect(Collectors.toList());

        return Response.ok(queryUserResults).build();
    }

    @GET
    @Path("{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response getUser(@PathParam("userId") Long userId) {

        UserAccount user = userService.findById(userId).orElseThrow(NotFoundException::new);
        QueryUserResult queryUserResult = toQueryUserResult(user);
        return Response.ok(queryUserResult).build();
    }

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getAuthenticatedUser() {

        Principal principal = securityContext.getUserPrincipal();

        if (principal == null) {
            QueryUserResult queryUserResult = new QueryUserResult();
            queryUserResult.setUsername("anonymous");
            queryUserResult.setAuthorities(new HashSet<>());
            return Response.ok(queryUserResult).build();
        }

        UserAccount user = userService.findByUsername(principal.getName());
        QueryUserResult queryUserResult = toQueryUserResult(user);
        return Response.ok(queryUserResult).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response addUser(UserAccount user) {
		userService.addUser(user);
		QueryUserResult queryUserResult = toQueryUserResult(user);
		return Response.ok(queryUserResult).build();
    }
    
    @GET
    @Path("month/{userName}/{quantity}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response extendLicenseForMonths(@PathParam("userName") String userName, @PathParam("quantity") int months) {
		userService.extendLicenseMonth(userName, months);
		return Response.ok().build();
    }
    
    @GET
    @Path("week/{userName}/{quantity}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response extendLicenseForWeeks(@PathParam("userName") String userName, @PathParam("quantity") int weeks) {
		userService.extendLicenseWeek(userName, weeks);;
		return Response.ok().build();
    }
    
    @GET
    @Path("reset/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response resetLicense(@PathParam("userName") String userName) {
		userService.resetLicense(userName);
		return Response.ok().build();
    }
    
    
    
    /**
     * Maps a {@link UserAccount} instance to a {@link QueryUserResult} instance.
     *
     * @param user
     * @return
     */
    private QueryUserResult toQueryUserResult(UserAccount user) {
        QueryUserResult queryUserResult = new QueryUserResult();
        queryUserResult.setId(user.getId());
        queryUserResult.setUsername(user.getUsername());
        queryUserResult.setAuthorities(user.getAuthorities());
        queryUserResult.setExpiringDate(user.getExpiringDate());
        return queryUserResult;
    }
}