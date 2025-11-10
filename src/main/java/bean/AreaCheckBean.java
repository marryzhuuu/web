package bean;

import entity.Result;
import service.DatabaseService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class AreaCheckBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Double x = 0.0;
    private Double y = 0.0;
    private Double r = 2.0;

    @Inject
    private DatabaseService databaseService;

    @Inject
    private ResultsBean resultsBean;

    public void checkPoint() {
        long startTime = System.nanoTime();

        boolean result = checkArea(x, y, r);
        long executionTime = (System.nanoTime() - startTime) / 1000; // microseconds

        Result pointResult = new Result(x, y, r, result, executionTime);
        databaseService.saveResult(pointResult);
        resultsBean.addResult(pointResult);
    }

    public void checkPointFromGraph(double x, double y) {
        this.x = x;
        this.y = y;
        checkPoint();
    }

    private boolean checkArea(double x, double y, double r) {
        // First quadrant: rectangle
        if (x >= 0 && y >= 0) {
            return x <= r && y <= r/2;
        }
        // Second quadrant: triangle
        if (x <= 0 && y >= 0) {
            return y <= x + r;
        }
        // Fourth quadrant: quarter circle
        if (x >= 0 && y <= 0) {
            return (x * x + y * y) <= (r * r);
        }
        return false;
    }

    // Getters and Setters
    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Double getR() { return r; }
    public void setR(Double r) {
        this.r = r;
        // Trigger graph update when R changes
        resultsBean.updateResults();
    }
}