package rest;

import entity.User;
import service.UserService;
import util.JwtUtil;
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

    @EJB
    private JwtUtil jwtUtil;

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            logger.info("Login attempt for user: " + request.getUsername());

            // Валидация входных данных
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return createErrorResponse("Username is required");
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return createErrorResponse("Password is required");
            }

            // Проверка пользователя
            if (userService.validateUser(request.getUsername(), request.getPassword())) {
                // Генерация JWT токена
                String token = jwtUtil.generateToken(request.getUsername());

                User user = userService.findByUsername(request.getUsername());

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("token", token);
                response.put("user", createUserResponse(user));

                logger.info("Login successful for user: " + request.getUsername());
                return Response.ok(response).build();
            } else {
                return createErrorResponse("Invalid username or password");
            }
        } catch (Exception e) {
            logger.severe("Error during login: " + e.getMessage());
            return createErrorResponse("Internal server error");
        }
    }

    @POST
    @Path("/register")
    public Response register(LoginRequest request) {
        try {
            logger.info("Registration attempt for user: " + request.getUsername());

            // Валидация
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return createErrorResponse("Username is required");
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return createErrorResponse("Password is required");
            }

            if (request.getUsername().length() < 3) {
                return createErrorResponse("Username must be at least 3 characters long");
            }

            if (request.getPassword().length() < 6) {
                return createErrorResponse("Password must be at least 6 characters long");
            }

            if (userService.findByUsername(request.getUsername()) != null) {
                return createErrorResponse("User already exists");
            }

            // Создание пользователя
            User user = userService.createUser(request.getUsername(), request.getPassword());

            // Генерация токена
            String token = jwtUtil.generateToken(request.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User created successfully");
            response.put("token", token);
            response.put("user", createUserResponse(user));

            logger.info("User registered successfully: " + request.getUsername());
            return Response.ok(response).build();

        } catch (IllegalArgumentException e) {
            return createErrorResponse(e.getMessage());
        } catch (Exception e) {
            logger.severe("Error during registration: " + e.getMessage());
            return createErrorResponse("Internal server error");
        }
    }

    @GET
    @Path("/validate")
    public Response validateToken(@HeaderParam("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return createErrorResponse("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Invalid or expired token\"}")
                        .build();
            }

            String username = jwtUtil.extractUsername(token);
            User user = userService.findByUsername(username);

            if (user == null) {
                return createErrorResponse("User not found");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("user", createUserResponse(user));

            return Response.ok(response).build();

        } catch (Exception e) {
            logger.severe("Error validating token: " + e.getMessage());
            return createErrorResponse("Internal server error");
        }
    }

    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("username", user.getUsername());
        userResponse.put("createdAt", user.getCreatedAt());
        return userResponse;
    }

    private Response createErrorResponse(String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }

    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}