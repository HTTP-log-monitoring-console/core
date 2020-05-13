package com.homework.monitoring.alerts;

import com.homework.monitoring.stats.HTTPTrafficStats;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Alerts manager tests.
 * <p>
 * For more complex tests I would use something like PowerMock or Mockito, but for basic stuff JUnit is enough.
 */
class HTTPTrafficAlertsManagerTest {
    private static boolean alertRaised = false;
    private static HTTPTrafficAlertsManager alertsManager;
    private static HTTPTrafficAlertsListener alertsListener;

    private static final int MONITORING_WINDOW = 30; // in seconds.
    private static final int ALERT_THRESHOLD = 2;

    @BeforeEach
    void beforeEach() {
        alertsListener = new HTTPTrafficAlertsListener() {
            @Override
            public void raiseAlert() {
                alertRaised = true;
            }

            @Override
            public void cancelAlert() {
                alertRaised = false;
            }
        };

        // we are initializing an alert manager which will watch a sliding window of 30 seconds and process each
        // traffic stats object as if coming every 10 seconds; the alert threshold is at 2 requests per second.
        alertsManager = new HTTPTrafficAlertsManager(MONITORING_WINDOW, ALERT_THRESHOLD);
        alertsManager.registerAlertsListener(alertsListener);
    }

    @AfterEach
    void afterEach() {
        alertRaised = false;
    }

    @Test
    void testInitialization() {
        assertFalse(alertRaised);
        assertEquals(0, alertsManager.getNumberOfRequestsInWindow());
    }

    @Test
    void testRaisedAlarm() {
        assertFalse(alertRaised);

        // the alerts manager is configured to accept new stats every 10 seconds and to use a monitoring window of
        // two minutes during which alarms can be raised
        alertsManager.processTrafficStatistics(new HTTPTrafficStats(2000, 0, null));

        assertTrue(alertRaised);
        assertEquals(2000, alertsManager.getNumberOfRequestsInWindow());
    }

    @Test
    void testAlarmBeingRaisedAndCancelled() {
        assertFalse(alertRaised);

        // the alerts manager is configured to accept new stats every 10 seconds and to use a monitoring window of
        // two minutes during which alarms can be raised
        alertsManager.processTrafficStatistics(new HTTPTrafficStats(2, 0, null));
        alertsManager.processTrafficStatistics(new HTTPTrafficStats(20, 0, null));
        alertsManager.processTrafficStatistics(new HTTPTrafficStats(20, 0, null));

        // we are not over the 5 requests per second time
        assertFalse(alertRaised);
        assertEquals(42, alertsManager.getNumberOfRequestsInWindow());

        alertsManager.processTrafficStatistics(new HTTPTrafficStats(2, 0, null));
        assertFalse(alertRaised);
        assertEquals(42, alertsManager.getNumberOfRequestsInWindow());

        alertsManager.processTrafficStatistics(new HTTPTrafficStats(40, 0, null));
        assertTrue(alertRaised);
        assertEquals(62, alertsManager.getNumberOfRequestsInWindow());

        alertsManager.processTrafficStatistics(new HTTPTrafficStats(5, 0, null));
        assertFalse(alertRaised);
        assertEquals(47, alertsManager.getNumberOfRequestsInWindow());
    }

    /**
     * More tests can be added to verify that listeners are correctly registered (and test null, duplicated and invalid
     * listeners), test alerts manager constructor with invalid data and mock it with the other managers.
     */
}