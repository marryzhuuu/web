package mbean;

public interface PointStatsMBean {
    long getTotalPoints();
    long getMissedPoints();
    int getConsecutiveMisses();
    void reset();
}
