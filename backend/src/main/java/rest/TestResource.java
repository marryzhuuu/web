package rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Path("/test")
public class TestResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTestData() {
        try {
            Map<String, String> response = new HashMap<>();

            String currentTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            response.put("currentTime", currentTime);
            response.put("testString", "Это тестовая строка из REST API");
            response.put("status", "success");

            Jsonb jsonb = JsonbBuilder.create();
            return jsonb.toJson(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());

            try {
                Jsonb jsonb = JsonbBuilder.create();
                return jsonb.toJson(errorResponse);
            } catch (Exception ex) {
                return "{\"status\":\"error\",\"message\":\"JSON conversion failed\"}";
            }
        }
    }
}