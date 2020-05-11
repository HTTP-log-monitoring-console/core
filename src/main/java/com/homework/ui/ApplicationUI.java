package com.homework.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.homework.monitoring.alerts.HTTPTrafficAlertsListener;
import com.homework.monitoring.stats.HTTPStatsListener;
import com.homework.monitoring.stats.HTTPTrafficStats;

import java.io.IOException;

/**
 * Class managing the console UI and using Google's Lanterna libraries.
 *
 * This object will receive notifications when new traffic statistics are created and when alerts are raised or cancelled.
 */
public class ApplicationUI implements HTTPStatsListener, HTTPTrafficAlertsListener {
    /**
     * Console window.
     */
    private ApplicationWindow window = new ApplicationWindow();

    /**
     * Create a new console window.
     * @throws IOException exceptions thrown if the console window initialization fails.
     */
    public void create() throws IOException {
        // create a new terminal
        Terminal console = new DefaultTerminalFactory().createTerminal();
        // create the screen and initialize it
        Screen screen = new TerminalScreen(console);
        screen.startScreen();

        // the window will react to the console being resized
        console.addResizeListener(window);

        // force the correct size on window initialization
        window.onResized(console, screen.getTerminalSize());

        // start the UI.
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);
        System.exit(0);
    }

    @Override
    public void raiseAlert() {
        window.handleAlertRaised();
    }

    @Override
    public void cancelAlert() {
        window.handleAlertCanceled();
    }

    @Override
    public void processTrafficStatistics(HTTPTrafficStats httpTrafficStats) {
        window.handleTrafficStatistics(httpTrafficStats);
    }
}