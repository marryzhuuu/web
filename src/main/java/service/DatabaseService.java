package service;

import entity.Result;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DatabaseService {

    private static final String URL = "jdbc:oracle:thin:@localhost:38177/xepdb1";
    private static final String USER = "s381731";
    private static final String PASSWORD = "s381731";

    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE point_results (
            id NUMBER PRIMARY KEY,
            x NUMBER NOT NULL,
            y NUMBER NOT NULL,
            r NUMBER NOT NULL,
            result NUMBER(1) NOT NULL,
            timestamp TIMESTAMP NOT NULL,
            execution_time NUMBER NOT NULL
        )
    """;

    private static final String INSERT_SQL = """
        INSERT INTO point_results (id, x, y, r, result, timestamp, execution_time) 
        VALUES (point_seq.nextval, ?, ?, ?, ?, ?, ?)
    """;

    private static final String SELECT_ALL_SQL = "SELECT * FROM point_results ORDER BY timestamp DESC";
    private static final String CLEAR_SQL = "DELETE FROM point_results";

    private Connection connection;

    public DatabaseService() {
        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            createTableIfNotExists();
            System.out.println("Database connection established");
        } catch (Exception e) {
            System.err.printf("Failed to initialize database connection: %s%n", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    private void createTableIfNotExists() {
        try (Statement stmt = connection.createStatement()) {
            // Try to create table (will fail if exists, but that's OK)
            stmt.execute(CREATE_TABLE_SQL);
            System.out.println("Table created successfully");
        } catch (SQLException e) {
            // Table likely already exists
            System.out.println("Table already exists or creation failed: " + e.getMessage());
        }
    }

    public void saveResult(Result result) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setDouble(1, result.getX());
            stmt.setDouble(2, result.getY());
            stmt.setDouble(3, result.getR());
            stmt.setInt(4, result.isResult() ? 1 : 0);
            stmt.setTimestamp(5, Timestamp.valueOf(result.getTimestamp()));
            stmt.setLong(6, result.getExecutionTime());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save point result" + e.getMessage());
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
                result.setResult(rs.getInt("result") == 1);
                result.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                result.setExecutionTime(rs.getLong("execution_time"));
                results.add(result);
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve point results" + e.getMessage());
        }
        return results;
    }

    public void clearResults() {
        try (PreparedStatement stmt = connection.prepareStatement(CLEAR_SQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to clear results" + e.getMessage());
        }
    }
}