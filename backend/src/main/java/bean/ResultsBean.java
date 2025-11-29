package bean;

import entity.Result;
import service.DatabaseService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

// Заглушка для отладки до реализации подключения к БД
@Named("resultsBean")
@ApplicationScoped
public class ResultsBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Result> results;

    @Inject
    private DatabaseService databaseService;

    @PostConstruct
    public void init() {
        results = new ArrayList<>();
        System.out.println("ResultsBean constructed");
    }

    public void addResult(Result result) {
        if (result != null) {
            results.add(0, result); // Add to beginning for newest first
            System.out.println("Added result: " + result);
        }
    }

    public void updateResults() {
        try {
            if (databaseService != null) {
                results.clear();
                results.addAll(databaseService.getAllResults());
                System.out.println("Results updated. Total: " + results.size());
            }
        } catch (Exception e) {
            System.err.println("Error updating results: " + e.getMessage());
        }
    }

    public void clearResults() {
        try {
            if (databaseService != null) {
                databaseService.clearResults();
                results.clear();
                System.out.println("Results cleared");
            }
        } catch (Exception e) {
            System.err.println("Error clearing results: " + e.getMessage());
        }
    }

    public List<Result> getResults() {
        return results.stream()
                .sorted(Comparator.comparing(Result::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<Result> getUpdatedResults(Double r) {
        try {
            List<Result> recalculated = new ArrayList<>();

            for (Result original : results) {
                // Создаем новый объект Result с теми же x, y, но пересчитываем hit с новым r
                // Исходные объекты Result остаются неизменными
                Result recalculatedResult = new Result(
                        original.getX(),
                        original.getY(),
                        r,  // используем новый радиус
                        AreaCheckBean.checkArea(original.getX(), original.getY(), r),  // пересчитываем попадание
                        original.getExecutionTime()
                );
                recalculated.add(recalculatedResult);
            }

            return recalculated;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    // Геттер для сервиса (если нужен)
    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
}