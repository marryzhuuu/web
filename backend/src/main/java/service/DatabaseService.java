package service;

import entity.Result;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DatabaseService {

    // Используем JNDI для получения DataSource
    private static final String DATASOURCE_JNDI = "java:jboss/datasources/PostgreSQLDS";

    // Обновленные SQL запросы для PostgreSQL
    private static final String CREATE_SEQUENCE_SQL = """
        CREATE SEQUENCE IF NOT EXISTS point_seq START 1 INCREMENT 1
    """;

    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS point_results (
            id BIGINT PRIMARY KEY DEFAULT nextval('point_seq'),
            x DOUBLE PRECISION NOT NULL,
            y DOUBLE PRECISION NOT NULL,
            r DOUBLE PRECISION NOT NULL,
            result BOOLEAN NOT NULL,
            timestamp TIMESTAMP NOT NULL,
            execution_time BIGINT NOT NULL
        )
    """;

    private static final String INSERT_SQL = """
        INSERT INTO point_results (x, y, r, result, timestamp, execution_time) 
        VALUES (?, ?, ?, ?, ?, ?)
    """;

    private static final String SELECT_ALL_SQL = "SELECT * FROM point_results ORDER BY timestamp DESC";
    private static final String CLEAR_SQL = "DELETE FROM point_results";

    private Connection connection;
    private DataSource dataSource;

    public DatabaseService() {
        try {
            // Получаем DataSource через JNDI
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(DATASOURCE_JNDI);

            connection = dataSource.getConnection();
            createTableIfNotExists();
            System.out.println("PostgreSQL database connection established via WildFly DataSource");
        } catch (Exception e) {
            System.err.printf("Failed to initialize database connection: %s%n", e);
            e.printStackTrace();
        }
    }

    // Остальные методы остаются без изменений...
    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_SEQUENCE_SQL);
            stmt.execute(CREATE_TABLE_SQL);
            System.out.println("Table and sequence created successfully");
        } catch (SQLException e) {
            System.err.println("Table creation failed: " + e.getMessage());
        }
    }

    public void saveResult(Result result) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setDouble(1, result.getX());
            stmt.setDouble(2, result.getY());
            stmt.setDouble(3, result.getR());
            stmt.setBoolean(4, result.isResult());
            stmt.setTimestamp(5, Timestamp.valueOf(result.getTimestamp()));
            stmt.setLong(6, result.getExecutionTime());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save point result: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Result> getAllResults() {
        List<Result> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Result result = new Result();
                result.setId(rs.getLong("id"));
                result.setX(rs.getDouble("x"));
                result.setY(rs.getDouble("y"));
                result.setR(rs.getDouble("r"));
                result.setResult(rs.getBoolean("result"));
                result.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                result.setExecutionTime(rs.getLong("execution_time"));
                results.add(result);
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve point results: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    public void clearResults() {
        try (PreparedStatement stmt = connection.prepareStatement(CLEAR_SQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to clear results: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}