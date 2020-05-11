package com.homework.monitoring.alerts;

/**
 * Interface for an alert lister.
 */
public interface HTTPTrafficAlertsListener {
    /**
     * Raise alert means a new alert was created because the number of requests has gone over the
     * threshold during the monitoring window.
     */
    void raiseAlert();

    /**
     * A previously raised alert has been canceled because the number of requests has gone below
     * the threshold during the (sliding) monitoring window.
     */
    void cancelAlert();
}
