package model;

import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultManager {
    private static final String RESULTS_ATTRIBUTE = "areaCheckResults";
    private static final int MAX_RESULTS = 100; // Ограничение на количество результатов

    // Добавить результат
    public static void addResult(ServletContext context, Result result) {
        List<Result> results = getResults(context);
        results.add(result); // Добавляем в конец (новые результаты будут в конце)

        // Ограничиваем размер списка
        if (results.size() > MAX_RESULTS) {
            results = new ArrayList<>(results.subList(results.size() - MAX_RESULTS, results.size()));
            context.setAttribute(RESULTS_ATTRIBUTE, results);
        }
    }

    // Получить все результаты в порядке возрастания даты (старые сначала)
    @SuppressWarnings("unchecked")
    public static List<Result> getResults(ServletContext context) {
        List<Result> results = (List<Result>) context.getAttribute(RESULTS_ATTRIBUTE);
        if (results == null) {
            results = Collections.synchronizedList(new ArrayList<>());
            context.setAttribute(RESULTS_ATTRIBUTE, results);
        }

        // Возвращаем результаты в естественном порядке (старые сначала)
        return results;
    }

    // Очистить историю
    public static void clearResults(ServletContext context) {
        context.setAttribute(RESULTS_ATTRIBUTE, new ArrayList<Result>());
    }
}