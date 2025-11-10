package bean;

import entity.Result;
import service.DatabaseService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@ApplicationScoped
public class ResultsBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Result> results;

    @Inject
    private DatabaseService databaseService;

    @PostConstruct
    public void init() {
        results = new CopyOnWriteArrayList<>(databaseService.getAllResults());
    }

    public void addResult(Result result) {
        results.add(0, result); // Add to beginning for newest first
    }

    public void updateResults() {
        results.clear();
        results.addAll(databaseService.getAllResults());
    }

    public void clearResults() {
        databaseService.clearResults();
        results.clear();
    }

    public List<Result> getResults() {
        return results;
    }
}