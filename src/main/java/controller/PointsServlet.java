package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;
import model.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@WebServlet(name = "PointsServlet", value = "/points")
public class PointsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            HttpSession session = request.getSession();
            ArrayList<Result> results = (ArrayList<Result>) session.getAttribute("results");

            // Use a JSON library like Gson or Jackson to serialize the list to JSON
            Gson gson = new Gson();
            String json = gson.toJson(results);  // Convert the list to JSON
//
            response.getWriter().write(json); // Send the JSON response
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    private boolean checkHit(double x, double y, double r) {
        // Прямоугольная область (первая четверть)
        if (x >= 0 && y >= 0 && x <= r/2 && y <= r) {
            return true;
        }

        // Треугольная область (четвертая четверть)
        if (x >= 0 && y <= 0 && y >= x - r/2) {
            return true;
        }

        // Круговая область (третья четверть)
        if (x <= 0 && y <= 0 && (x*x + y*y) <= (r/2)*(r/2)) {
            return true;
        }

        return false;
    }
}