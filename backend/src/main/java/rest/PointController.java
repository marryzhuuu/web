package rest;

import entity.Result;
import entity.User;
import service.PointCheckService;
import service.UserService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/points")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PointController {

    @EJB
    private PointCheckService pointCheckService;

    @EJB
    private UserService userService;

    @POST
    @Path("/check")
    public Response checkPoint(PointRequest request, @HeaderParam("X-Username") String username) {
        // JwtFilter уже проверил авторизацию и установил X-Username
//        if (username == null) {
//            return Response.status(Response.Status.UNAUTHORIZED).build();
//        }

        User user = userService.findByUsername(username);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Валидация данных
        if (!isValidPoint(request.getX(), request.getY(), request.getR())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid coordinates or radius\"}")
                    .build();
        }

        Result result = pointCheckService.checkPoint(request.getX(), request.getY(), request.getR(), user);

        Map<String, Object> response = new HashMap<>();
        response.put("x", result.getX());
        response.put("y", result.getY());
        response.put("r", result.getR());
        response.put("result", result.getResult());
        response.put("checkTime", result.getCheckTime());

        return Response.ok(response).build();
    }

    @GET
    @Path("/history")
    public Response getHistory(@HeaderParam("X-Username") String username, @QueryParam("r") Double radius) {
        // JwtFilter уже проверил авторизацию и установил X-Username
//        if (username == null) {
//            return Response.status(Response.Status.UNAUTHORIZED).build();
//        }

        User user = userService.findByUsername(username);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<Result> checks = pointCheckService.getUserChecks(user);
        List<Map<String, Object>> response;

        // Если передан параметр r, пересчитываем результаты
        if (radius != null) {
            response = checks.stream().map(check -> {
                // Пересчитываем результат с новым радиусом
                boolean recalculatedResult = PointCheckService.checkHit(
                        check.getX(),
                        check.getY(),
                        radius
                );
                Map<String, Object> item = new HashMap<>();
                item.put("x", check.getX());
                item.put("y", check.getY());
                item.put("r", radius);
                item.put("result", recalculatedResult);
                item.put("checkTime", check.getCheckTime());
                return item;
            }).collect(Collectors.toList());

        } else {
            // Если параметр r не передан, возвращаем оригинальные данные
            response = checks.stream().map(check -> {
                Map<String, Object> item = new HashMap<>();
                item.put("x", check.getX());
                item.put("y", check.getY());
                item.put("r", check.getR());
                item.put("result", check.getResult());
                item.put("checkTime", check.getCheckTime());
                return item;
            }).collect(Collectors.toList());

        }
        return Response.ok(response).build();
    }

    private boolean isValidPoint(Double x, Double y, Double r) {
        // Проверка допустимых значений
        return x != null && y != null && r != null &&
                x >= -4 && x <= 4 &&
                y >= -5 && y <= 5 &&
                r >= 1 && r <= 4;
    }

    public static class PointRequest {
        private Double x;
        private Double y;
        private Double r;

        // Getters and setters
        public Double getX() { return x; }
        public void setX(Double x) { this.x = x; }
        public Double getY() { return y; }
        public void setY(Double y) { this.y = y; }
        public Double getR() { return r; }
        public void setR(Double r) { this.r = r; }
    }
}