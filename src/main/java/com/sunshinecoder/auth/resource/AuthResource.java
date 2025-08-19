package com.sunshinecoder.auth.resource;

import com.sunshinecoder.auth.dto.AuthResponse;
import com.sunshinecoder.auth.dto.UserLoginRequest;
import com.sunshinecoder.auth.dto.UserRegistrationRequest;
import com.sunshinecoder.auth.service.AuthService;
import com.sunshinecoder.common.dto.response.ApiResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    public Response register(@Valid UserRegistrationRequest request) {
        try {
            AuthResponse authResponse = authService.registerUser(request);
            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.created("User registered successfully", authResponse))
                    .build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.badRequest(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.internalServerError("Registration failed: "+ e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/login")
    public Response login(@Valid UserLoginRequest request) {
        try {
            AuthResponse authResponse = authService.loginUser(request);
            return Response.ok(ApiResponse.success("Authentication successful", authResponse)).build();
        } catch (NotAuthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.unauthorized("Invalid credentials"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.internalServerError("Login failed"))
                    .build();
        }
    }

    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String authHeader) {
        try {
            String token = authHeader != null && authHeader.startsWith("Bearer ")
                    ? authHeader.substring(7)
                    : null;

            authService.logout(token);
            return Response.ok(ApiResponse.success("Logged out successfully", null)).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Logout failed: " + e.getMessage()))
                    .build();
        }
    }

}
