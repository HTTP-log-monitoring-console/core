package com.homework.monitoring.alerts;

import com.homework.monitoring.stats.HTTPStatsListener;
import com.homework.monitoring.stats.HTTPTrafficStats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The traffic alerts manager will listen for new stats and examine the ones having arrived in the
 * active alert window (i.e. two minutes). If the number of requests goes over a limit, it will
 * raise an alert; if the number of requests during that time it has gone under the alert threshold,
 * it will cancel the alert.
 *
 * @Note: an alternate architecture would have been to listen for CLF entries directly and just count
 * them as they come, which would offer a more fine grained reaction in raising alerts; however, as
 * the stats manager is already listening for logs and aggregating data, this simplifies the logic.
 */
public class HTTPTrafficAlertsManager implements HTTPStatsListener {
    private int alertThreshold = 5;
    private int numberOfRequestsInWindow = 0;
    private boolean alertStateEnabled = false;

    /**
     * The monitoring period is the window in which the number of requests must be over the alert
     * threshold in order for the alert to be raised.
     */
    private static final int DEFAULT_ALERT_MONITORING_WINDOW = 2 * 60; // in seconds

    /**
     * Default period (in seconds) between incoming traffic stats updates.
     */
    private static final int DEFAULT_PERIOD_BETWEEN_STATS_UPDATES = 10; // in seconds

    /**
     * Logger.
     */
    private Logger logger = LogManager.getLogger(HTTPStatsListener.class);

    /**
     * Queue of incoming stats.
     *
     * @Note because the alert manager is a listener for traffic stats and the contract of the traffic
     * stats manager is to send new updates every 10 seconds, in order to use a sliding window for the
     * last N minutes we simply limit the number of traffic stats we take into account.
     *
     * For example, if the window is 2 minutes, which is 120 seconds, that means we will look at the last
     * 120 seconds / 10 seconds between each update = 12 traffic stats updates.
     */
    private Queue<HTTPTrafficStats> listTrafficStats;

    /**
     * List of alert listeners.
     */
    private List<HTTPTrafficAlertsListener> alertsListenerList = new ArrayList<>();

    /**
     * Length (in seconds) of the monitoring window during which traffic is being inspected to raise alerts.
     */
    private final int alertMonitoringWindow;

    /**
     * Constructor.
     * @param alertMonitoringWindow the user set monitoring window for determining if an alert should be raised (in seconds).
     * @param maxRequestsPerSecondForAlert the number of requests which, if gone over, an alert must be raised.
     */
    public HTTPTrafficAlertsManager(final int alertMonitoringWindow, final int maxRequestsPerSecondForAlert) {
        this.alertThreshold = maxRequestsPerSecondForAlert;

        if (alertMonitoringWindow < 10) {
            logger.error("Alert monitoring window must be of at least 10 seconds length.");
            this.alertMonitoringWindow = DEFAULT_ALERT_MONITORING_WINDOW;
        } else {
            if (alertMonitoringWindow > 10 * 60) {
                logger.warn("Alert monitoring window is longer than 10 minutes, please be patient while the stats are being gathered");
            }
            this.alertMonitoringWindow = alertMonitoringWindow;
        }

        if (maxRequestsPerSecondForAlert < 1) {
            logger.error("Alert threshold is not valid (" + maxRequestsPerSecondForAlert + "), setting it to default of 5.");
            this.alertThreshold = 5;
        } else if (maxRequestsPerSecondForAlert > 20) {
            logger.warn("Alert threshold is very high (" + maxRequestsPerSecondForAlert + ").");
        }
        listTrafficStats = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void processTrafficStatistics(final HTTPTrafficStats httpTrafficStats) {
        logger.info("Processing new traffic stats - received " + httpTrafficStats.getTotalNumberOfHTTPRequests() + " new requests");

        // we only want to maintain a sliding window for the monitoring window, so if we get updates every N seconds and
        // if we have a predefined monitoring window of M seconds then we must keep max M/N new stat updates.
        if (listTrafficStats.size() >= alertMonitoringWindow / DEFAULT_PERIOD_BETWEEN_STATS_UPDATES) {
            // take oldest stats and remove its details from the alert monitoring.
            final HTTPTrafficStats oldestStats = listTrafficStats.remove();
            numberOfRequestsInWindow -= oldestStats.getTotalNumberOfHTTPRequests();
        }
        listTrafficStats.add(httpTrafficStats);

        numberOfRequestsInWindow += httpTrafficStats.getTotalNumberOfHTTPRequests();
        float requestsPerSecond = (float) numberOfRequestsInWindow / alertMonitoringWindow;
        logger.info("Requests per second = " + requestsPerSecond);

        if (requestsPerSecond < alertThreshold) {
            if (alertStateEnabled) {
                alertStateEnabled = false;
                logger.error("Alert disabled " + requestsPerSecond);
                cancelAlert();
            }
        } else {
            if (!alertStateEnabled) {
                alertStateEnabled = true;
                logger.error("Alert enabled " + requestsPerSecond);
                raiseAlert();
            }
        }
    }

    /**
     * Register a new {@link HTTPTrafficAlertsListener}.
     * @param alertsListener the listener; must be non-null.
     *
     * @apiNote duplicated listeners will only be registered once.
     */
    public void registerAlertsListener(final HTTPTrafficAlertsListener alertsListener) {
        if (alertsListener == null) {
            logger.error("Cannot accept null alerts listener");
        } else if (alertsListenerList.contains(alertsListener)) {
            logger.warn("Same alerts listener already registered; skipping second registration.");
        } else {
            alertsListenerList.add(alertsListener);
        }
    }

    /**
     * Notify all listeners about a new alert.
     */
    protected void raiseAlert() {
        for (final HTTPTrafficAlertsListener listener : alertsListenerList) {
            listener.raiseAlert();
        }
    }

    /**
     * Cancel previously raised alert.
     */
    protected void cancelAlert() {
        for (final HTTPTrafficAlertsListener listener : alertsListenerList) {
            listener.cancelAlert();
        }
    }

    /**
     * @return the current number of requests tracked during the active monitoring window {@link }
     */
    public int getNumberOfRequestsInWindow() {
        return numberOfRequestsInWindow;
    }
}
