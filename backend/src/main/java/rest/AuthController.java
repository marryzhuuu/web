package rest;

import entity.User;
import service.UserService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    @EJB
    private UserService userService;

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            logger.info("Login attempt for user: " + request.getUsername());

            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Username is required\"}")
                        .build();
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Password is required\"}")
                        .build();
            }

            if (userService.validateUser(request.getUsername(), request.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("username", request.getUsername());
                logger.info("Login successful for user: " + request.getUsername());
                return Response.ok(response).build();
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Invalid username or password");
                logger.warning("Login failed for user: " + request.getUsername());
                return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
            }
        } catch (Exception e) {
            logger.severe("Error during login: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Internal server error");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }

    @POST
    @Path("/register")
    public Response register(LoginRequest request) {
        try {
            logger.info("Registration attempt for user: " + request.getUsername());

            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Username is required\"}")
                        .build();
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Password is required\"}")
                        .build();
            }

            if (request.getUsername().length() < 3) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Username must be at least 3 characters long\"}")
                        .build();
            }

            if (request.getPassword().length() < 6) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Password must be at least 6 characters long\"}")
                        .build();
            }

            if (userService.findByUsername(request.getUsername()) != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"User already exists\"}")
                        .build();
            }

            User user = userService.createUser(request.getUsername(), request.getPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User created successfully");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            logger.info("User registered successfully: " + request.getUsername());
            return Response.ok(response).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            logger.severe("Error during registration: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Internal server error\"}")
                    .build();
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        @Override
        public String toString() {
            return "LoginRequest{username='" + username + "', password='[PROTECTED]'}";
        }
    }
}