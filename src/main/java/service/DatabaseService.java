package service;

import entity.Result;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Заглушка для отладки до реализации подключения к БД
public class DatabaseService {

    private List<Result> temporaryStorage = new ArrayList<>();

    public DatabaseService() {
        System.out.println("DatabaseService (STUB) initialized");
    }

    public List<Result> getAllResults() {
        System.out.println("Returning " + temporaryStorage.size() + " results from stub");
        return new ArrayList<>(temporaryStorage);
    }

    public void clearResults() {
        temporaryStorage.clear();
        System.out.println("Stub storage cleared");
    }

    public void saveResult(Result result) {
        temporaryStorage.add(result);
        System.out.println("Result saved to stub: " + result);
    }
}