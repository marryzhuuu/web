package mbean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.Notification;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PointStatsTest {

    private PointStats stats;
    private List<Notification> received;

    @BeforeEach
    void setUp() {
        stats = new PointStats();
        received = new ArrayList<>();
        stats.addNotificationListener(
                (notification, handback) -> received.add(notification),
                null, null
        );
    }

    @Test
    void initialState_allCountersAreZero() {
        assertEquals(0, stats.getTotalPoints());
        assertEquals(0, stats.getMissedPoints());
        assertEquals(0, stats.getConsecutiveMisses());
    }

    @Test
    void recordHit_incrementsTotalOnly() {
        stats.recordPoint(true);
        assertEquals(1, stats.getTotalPoints());
        assertEquals(0, stats.getMissedPoints());
        assertEquals(0, stats.getConsecutiveMisses());
    }

    @Test
    void recordMiss_incrementsTotalAndMissed() {
        stats.recordPoint(false);
        assertEquals(1, stats.getTotalPoints());
        assertEquals(1, stats.getMissedPoints());
        assertEquals(1, stats.getConsecutiveMisses());
    }

    @Test
    void recordHit_resetsConsecutiveMisses() {
        stats.recordPoint(false);
        stats.recordPoint(false);
        stats.recordPoint(true);
        assertEquals(0, stats.getConsecutiveMisses());
        assertEquals(2, stats.getMissedPoints());
        assertEquals(3, stats.getTotalPoints());
    }

    @Test
    void threeConsecutiveMisses_noNotification() {
        stats.recordPoint(false);
        stats.recordPoint(false);
        stats.recordPoint(false);
        assertTrue(received.isEmpty());
        assertEquals(3, stats.getConsecutiveMisses());
    }

    @Test
    void fourConsecutiveMisses_sendsNotification() {
        stats.recordPoint(false);
        stats.recordPoint(false);
        stats.recordPoint(false);
        stats.recordPoint(false);
        assertEquals(1, received.size());
        assertEquals(PointStats.NOTIFICATION_TYPE, received.get(0).getType());
    }

    @Test
    void fourConsecutiveMisses_notificationMessageContainsMissedCount() {
        stats.recordPoint(false);
        stats.recordPoint(false);
        stats.recordPoint(false);
        stats.recordPoint(false);
        String message = received.get(0).getMessage();
        assertNotNull(message);
        assertTrue(message.contains("4"));
    }

    @Test
    void fifthConsecutiveMiss_sendsSecondNotification() {
        for (int i = 0; i < 5; i++) stats.recordPoint(false);
        assertEquals(2, received.size());
    }

    @Test
    void hitAfterFourMisses_resetsConsecutiveButKeepsMissedTotal() {
        for (int i = 0; i < 4; i++) stats.recordPoint(false);
        stats.recordPoint(true);
        assertEquals(0, stats.getConsecutiveMisses());
        assertEquals(4, stats.getMissedPoints());
        assertEquals(5, stats.getTotalPoints());
    }

    @Test
    void reset_clearsAllCounters() {
        for (int i = 0; i < 4; i++) stats.recordPoint(false);
        stats.reset();
        assertEquals(0, stats.getTotalPoints());
        assertEquals(0, stats.getMissedPoints());
        assertEquals(0, stats.getConsecutiveMisses());
    }

    @Test
    void afterReset_fourConsecutiveMisses_sendsNotificationAgain() {
        for (int i = 0; i < 4; i++) stats.recordPoint(false);
        stats.reset();
        received.clear();
        for (int i = 0; i < 4; i++) stats.recordPoint(false);
        assertEquals(1, received.size());
    }

    @Test
    void mixedSequence_countsCorrectly() {
        // hit miss miss hit miss miss miss miss
        stats.recordPoint(true);
        stats.recordPoint(false);
        stats.recordPoint(false);
        stats.recordPoint(true);
        stats.recordPoint(false);
        stats.recordPoint(false);
        stats.recordPoint(false);
        stats.recordPoint(false);
        assertEquals(8, stats.getTotalPoints());
        assertEquals(6, stats.getMissedPoints());
        assertEquals(4, stats.getConsecutiveMisses());
        assertEquals(1, received.size());
    }
}
