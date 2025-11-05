package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import model.ResultManager;

@WebServlet(name = "ClearHistoryServlet", value = "/clear-history")
public class ClearHistoryServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Очищаем историю в контексте приложения
        ServletContext context = getServletContext();
        ResultManager.clearResults(context);

        // Возвращаем успешный ответ
        response.setContentType("application/json");
        response.getWriter().write("{\"status\": \"success\", \"message\": \"History cleared\"}");
    }
}