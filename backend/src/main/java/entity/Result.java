package entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Result implements Serializable {
    private Long id;
    private double x;
    private double y;
    private double r;
    private boolean result;
    private LocalDateTime timestamp;
    private long executionTime;

    public Result() {}

    public Result(double x, double y, double r, boolean result, long executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.result = result;
        this.timestamp = LocalDateTime.now();
        this.executionTime = executionTime;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getR() { return r; }
    public void setR(double r) { this.r = r; }

    public boolean isResult() { return result; }
    public void setResult(boolean result) { this.result = result; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
}