package com.homework.ui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import com.homework.monitoring.stats.HTTPTrafficStats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

/**
 * Class creating a console text window.
 */
public class ApplicationWindow extends BasicWindow implements TerminalResizeListener {
    /**
     * Logger.
     */
    private Logger logger = LogManager.getLogger(ApplicationWindow.class);

    /**
     * Panel containing pertinent information about the last 10 seconds of traffic as well as alerts.
     */
    private final Panel infoPanel;

    /**
     * Panel containing important messages raised by the application.
     */
    private final Panel messagesPanel;

    /**
     * Used to print duration since the monitoring is running.
     */
    private final Instant startedAt = Instant.now();

    /**
     * Last available stats being displayed.
     */
    private HTTPTrafficStats lastStats;

    public ApplicationWindow() {
        // create a main panel
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));

        infoPanel = new Panel();
        mainPanel.addComponent(infoPanel.withBorder(Borders.singleLine("Traffic stats and alerts")));

        messagesPanel = new Panel();
        messagesPanel.addComponent(new Label("The application is started; you can quit the terminal to stop it."));
        mainPanel.addComponent(messagesPanel.withBorder(Borders.singleLine("Messages")));

        setComponent(mainPanel);
        setHints(Arrays.asList(Hint.FULL_SCREEN, Hint.NO_DECORATIONS));
    }

    /**
     * Prints traffic statistics.
     *
     * @param statistics the statistics summary to print
     */
    void handleTrafficStatistics(HTTPTrafficStats statistics) {
        if (statistics == null) {
            logger.error("Invalid traffic stats ... ignoring.");
        }
        lastStats = statistics;

        infoPanel.removeAllComponents();
        final Duration elapsedTime = Duration.between(startedAt, Instant.now());
        infoPanel.addComponent(new Label(String.format("Elapsed time %sd %sh %sm %ss",
                elapsedTime.toDaysPart(),
                elapsedTime.toHoursPart(),
                elapsedTime.toMinutesPart(),
                elapsedTime.toSecondsPart())));
        infoPanel.addComponent(new Label("\nInformation").addStyle(SGR.BOLD));
        infoPanel.addComponent(new Label("Requests in last 10 seconds: " + lastStats.getTotalNumberOfHTTPRequestsInSnapshot()));
        infoPanel.addComponent(new Label("\nHits per section").addStyle(SGR.BOLD));
    }

    @Override
    public void onResized(Terminal terminal, TerminalSize newSize) {
        TerminalSize mainSize = new TerminalSize(newSize.getColumns(), newSize.getRows() - 10);
        infoPanel.setPreferredSize(mainSize);
        messagesPanel.setPreferredSize(new TerminalSize(newSize.getColumns(), 10));
    }

    public void handleAlertRaised() {
        handleTrafficStatistics(lastStats);

        messagesPanel.addComponent(new Label("\nTraffic alert raised !"));
    }

    public void handleAlertCanceled() {
        handleTrafficStatistics(lastStats);

        messagesPanel.addComponent(new Label("\nNo active alerts"));
    }
}
