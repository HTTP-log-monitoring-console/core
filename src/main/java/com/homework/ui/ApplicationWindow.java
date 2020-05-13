package com.homework.ui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import com.homework.monitoring.stats.HTTPTrafficStats;
import com.utils.ConversionUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Text;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Class creating a console text window.
 */
public class ApplicationWindow extends BasicWindow implements TerminalResizeListener {
    /**
     * Logger.
     */
    private Logger logger = LogManager.getLogger(ApplicationWindow.class);

    /**
     * Default number of terminal lines for the messages panel.
     */
    private static final int DEFAULT_MESSAGES_PANEL_ROWS = 10;

    /**
     * Panel containing pertinent information about the last 10 seconds of traffic.
     */
    private final Panel leftInfoPanel;

    /**
     * Panel containing pertinent information about section hits during last 10 seconds of traffic.
     */
    private final Panel rightInfoPanel;

    /**
     * Panel containing pertinent information about the last 10 seconds of traffic.
     */
    private final Panel infoPanel;

    /**
     * Panel containing important messages raised by the application (including alerts).
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

    /**
     * Last messages sent to the {@link messagesPanel}. Max {@link DEFAULT_MESSAGES_PANEL_ROWS} - 2 items.
     */
    private final CircularFifoQueue<AbstractMap.SimpleEntry<String, Boolean>> lastMessages =
            new CircularFifoQueue<>(DEFAULT_MESSAGES_PANEL_ROWS - 2);

    public ApplicationWindow() {
        // create a main panel
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));

        leftInfoPanel = new Panel();
        rightInfoPanel = new Panel();

        infoPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        infoPanel.addComponent(leftInfoPanel);
        infoPanel.addComponent(rightInfoPanel);
        mainPanel.addComponent(infoPanel.withBorder(Borders.singleLine("Traffic stats")));

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
    synchronized void handleTrafficStatistics(HTTPTrafficStats statistics) {
        if (statistics == null) {
            logger.error("Invalid traffic stats ... ignoring.");
        }
        lastStats = statistics;

        leftInfoPanel.removeAllComponents();
        rightInfoPanel.removeAllComponents();

        final Duration elapsedTime = Duration.between(startedAt, Instant.now());
        leftInfoPanel.addComponent(new Label("Current time : " + ConversionUtils.getInstantPrettyPrint()));
        leftInfoPanel.addComponent(new Label(String.format("Elapsed time %sd %sh %sm %ss",
                elapsedTime.toDaysPart(),
                elapsedTime.toHoursPart(),
                elapsedTime.toMinutesPart(),
                elapsedTime.toSecondsPart())));
        leftInfoPanel.addComponent(new Label("\nInformation (last 10 seconds)").addStyle(SGR.BOLD));
        leftInfoPanel.addComponent(new Label("Total requests: " + lastStats.getTotalNumberOfHTTPRequests()));
        leftInfoPanel.addComponent(new Label("Valid requests: " + lastStats.getTotalNumberOfValidHTTPRequests()));
        leftInfoPanel.addComponent(
                new Label("Client error requests: " + lastStats.getTotalNumberOfClientErrorHTTPRequests()).
                        setForegroundColor(TextColor.ANSI.YELLOW));
        leftInfoPanel.addComponent(
                new Label("Server error requests: " + lastStats.getTotalNumberOfValidHTTPRequests()).
                        setForegroundColor(TextColor.ANSI.RED));
        leftInfoPanel.addComponent(new Label("Overall size: " + lastStats.getTotalSizeOfRequestsInBytes() + " bytes."));

        final Map<String, Integer> hitsPerSection = lastStats.getHitsPerWebsiteSections();
        final Map<String, Integer> sorted = hitsPerSection.entrySet().stream().
                sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).
                collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (key, value) -> value, LinkedHashMap::new));
        leftInfoPanel.addComponent(new Label("\nAccessed sections stats").addStyle(SGR.BOLD));
        leftInfoPanel.addComponent(new Label("Total accessed sections: " + hitsPerSection.keySet().size()));

        rightInfoPanel.addComponent(new Label("\nAccessed sections").addStyle(SGR.BOLD));
        for (final String section : sorted.keySet()) {
            rightInfoPanel.addComponent(new Label(section + " - " + sorted.get(section) + " hits"));
        }
    }

    @Override
    public void onResized(Terminal terminal, TerminalSize newSize) {
        final int terminalColumns = newSize.getColumns();
        final int terminalRows = newSize.getRows();

        leftInfoPanel.setPreferredSize(new TerminalSize(terminalColumns / 2, terminalRows - DEFAULT_MESSAGES_PANEL_ROWS));
        rightInfoPanel.setPreferredSize(new TerminalSize(terminalColumns / 2, terminalRows - DEFAULT_MESSAGES_PANEL_ROWS));
        infoPanel.setPreferredSize(new TerminalSize(terminalColumns,terminalRows - DEFAULT_MESSAGES_PANEL_ROWS));
        messagesPanel.setPreferredSize(new TerminalSize(terminalColumns, DEFAULT_MESSAGES_PANEL_ROWS));
    }

    /**
     * Redraw messages panel with the latest messages.
     */
    private void handleLastMessages() {
        messagesPanel.removeAllComponents();
        for (int position = lastMessages.size() - 1; position >= 0; position--) {
            final Label messageLabel = new Label(lastMessages.get(position).getKey());
            if (lastMessages.get(position).getValue()) {
                messageLabel.setForegroundColor(TextColor.ANSI.RED);
            } else {
                messageLabel.setForegroundColor(TextColor.ANSI.BLUE);
            }
            messagesPanel.addComponent(messageLabel);
        }
    }

    /**
     * React to a new alert being raised.
     */
    public void handleAlertRaised() {
        lastMessages.add(new AbstractMap.SimpleEntry<>("Traffic alert raised ! " + ConversionUtils.getInstantPrettyPrint(), true));
        handleLastMessages();
    }

    /**
     * React to an ongoing alert being disabled.
     */
    public void handleAlertCanceled() {
        lastMessages.add(new AbstractMap.SimpleEntry<>("Deactivated alert. " + ConversionUtils.getInstantPrettyPrint(), false));
        handleLastMessages();
    }
}
