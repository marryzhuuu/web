package controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import model.Result;
import model.ResultManager;


@WebServlet(name = "PointsServlet", value = "/points")
public class PointsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            double r = Double.parseDouble(request.getParameter("r"));

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Сохраняем в контекст приложения
            ServletContext context = getServletContext();
            List<Result> results = ResultManager.getResults(context);

            // Создаем пересчитанные результаты (без изменения исходных)
            List<Result> recalculatedResults = createRecalculatedResults(results, r);


            Gson gson = new Gson();
            String json = gson.toJson(recalculatedResults);  // Convert the list to JSON

            response.getWriter().write(json); // Send the JSON response
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    private List<Result> createRecalculatedResults(List<Result> originalResults, double newR) {
        List<Result> recalculated = new ArrayList<>();

        for (Result original : originalResults) {
            // Создаем новый объект Result с теми же x, y, но пересчитываем hit с новым r
            // Исходные объекты Result остаются неизменными
            Result recalculatedResult = new Result(
                    original.getX(),
                    original.getY(),
                    newR,  // используем новый радиус
                    AreaCheckServlet.checkHit(original.getX(), original.getY(), newR),  // пересчитываем попадание
                    original.getTimestamp()  // сохраняем оригинальное время
            );
            recalculated.add(recalculatedResult);
        }

        return recalculated;
    }

}