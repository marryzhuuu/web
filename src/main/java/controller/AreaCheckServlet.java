package controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/");
    }

    private boolean checkHit(double x, double y, double r) {
        // Квадрат (вторая четверть)
        if (x <= 0 && y >= 0 && x >= -r && y <= r / 2) {
            return true;
        }

        // Треугольник (третья четверть)
        if (x <= 0 && y <= 0 && x >= -r && y >= -x - r) {
            return true;
        }

        // Круг (первая четверть)
        if (x >= 0 && y >= 0 && (x * x + y * y) <= (r) * (r)) {
            return true;
        }

        return false;
    }


}