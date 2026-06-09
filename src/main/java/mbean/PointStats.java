package mbean;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class PointStats extends NotificationBroadcasterSupport implements PointStatsMBean {

    public static final String NOTIFICATION_TYPE = "point.consecutive.misses";
    private static final int CONSECUTIVE_MISS_THRESHOLD = 4;

    private long totalPoints = 0;
    private long missedPoints = 0;
    private int consecutiveMisses = 0;
    private long notificationSequence = 0;

    public synchronized void recordPoint(boolean hit) {
        totalPoints++;
        if (!hit) {
            missedPoints++;
            consecutiveMisses++;
            if (consecutiveMisses == CONSECUTIVE_MISS_THRESHOLD) {
                Notification notification = new Notification(
                        NOTIFICATION_TYPE,
                        this,
                        notificationSequence++,
                        System.currentTimeMillis(),
                        "4 промаха подряд! Всего промахов: " + missedPoints
                );
                sendNotification(notification);
            }
        } else {
            consecutiveMisses = 0;
        }
    }

    @Override
    public synchronized long getTotalPoints() { return totalPoints; }

    @Override
    public synchronized long getMissedPoints() { return missedPoints; }

    @Override
    public synchronized int getConsecutiveMisses() { return consecutiveMisses; }

    @Override
    public synchronized void reset() {
        totalPoints = 0;
        missedPoints = 0;
        consecutiveMisses = 0;
        notificationSequence = 0;
    }
}
