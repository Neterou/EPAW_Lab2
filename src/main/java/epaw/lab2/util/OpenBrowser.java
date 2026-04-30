package epaw.lab2.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.awt.Desktop;
import java.net.URI;

/**
 * Opens the default browser at http://localhost:8080 automatically
 * when Jetty finishes deploying the application.
 *
 * Works on Windows, macOS, and most Linux desktops that have a browser
 * registered with {@link Desktop}. Silently skipped in headless environments
 * (e.g. CI servers).
 */
@WebListener
public class OpenBrowser implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (!Desktop.isDesktopSupported()) return;

        // Small delay so Jetty finishes binding the port before the browser opens
        new Thread(() -> {
            try {
                Thread.sleep(800);
                Desktop.getDesktop().browse(new URI("http://localhost:8080"));
            } catch (Exception e) {
                // Non-fatal – ignore silently
            }
        }, "open-browser").start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) { }
}