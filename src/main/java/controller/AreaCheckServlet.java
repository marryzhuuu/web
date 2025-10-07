package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import model.Result;

@WebServlet(name = "AreaCheckServlet", value = "/check")
public class AreaCheckServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            double x = Double.parseDouble(request.getParameter("x"));
            double y = Double.parseDouble(request.getParameter("y"));
            double r = Double.parseDouble(request.getParameter("r"));

            boolean result = checkHit(x, y, r);
            Result resultObj = new Result(x, y, r, result, new Date());

            HttpSession session = request.getSession();
            ArrayList<Result> results = (ArrayList<Result>) session.getAttribute("results");
            if (results == null) {
                results = new ArrayList<>();
            }
            results.add(resultObj);
            session.setAttribute("results", results);

            request.setAttribute("result", resultObj);
            request.setAttribute("x", x);
            request.setAttribute("y", y);
            request.setAttribute("r", r);

            request.getRequestDispatcher("/result.jsp").forward(request, response);

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