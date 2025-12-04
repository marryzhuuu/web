package filter;

import util.JwtUtil;
import jakarta.annotation.Priority;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.logging.Logger;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    private static final Logger logger = Logger.getLogger(JwtFilter.class.getName());

    @EJB
    private JwtUtil jwtUtil;

    // Пути, которые не требуют аутентификации
    private static final String[] PUBLIC_PATHS = {
            "/auth/login",
            "/auth/register",
            "/auth/validate",
            "/test"
    };

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        // Проверяем, является ли путь публичным
        if (isPublicPath(path)) {
            return;
        }

        // Получаем заголовок Authorization
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warning("Missing or invalid Authorization header for path: " + path);
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\": \"Authentication required\"}")
                            .build()
            );
            return;
        }

        // Извлекаем токен
        String token = authHeader.substring(7);

        // Валидируем токен
        if (!jwtUtil.validateToken(token)) {
            logger.warning("Invalid JWT token for path: " + path);
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\": \"Invalid or expired token\"}")
                            .build()
            );
            return;
        }

        // Добавляем username в заголовки для использования в контроллерах
        String username = jwtUtil.extractUsername(token);
        if (username != null) {
            requestContext.getHeaders().add("X-Username", username);
        }
    }

    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }
}