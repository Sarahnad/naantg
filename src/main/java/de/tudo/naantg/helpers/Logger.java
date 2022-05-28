/*
 * naantg (c) by Sarah Graf
 *
 * naantg is licensed under a
 * Creative Commons Attribution 4.0 International License.
 *
 * You should have received a copy of the license along with this
 * work. If not, see <http://creativecommons.org/licenses/by/4.0/>.
 */

package de.tudo.naantg.helpers;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Logs the warnings and information to the console.
 */
public class Logger {

    /**
     * Logs the warning or error message to the console.
     * @param message the warning or error message
     */
    public static void logWarning(String message) {
        log(Level.WARNING, message);
    }

    /**
     * Logs the information to the console.
     * @param message the information
     */
    public static void logInfo(String message) {
        log(Level.INFO, message);
    }

    /**
     * Logs the message as warning or information to the console.
     * @param level Level.WARNING or Level.INFO
     * @param message the message
     */
    private static void log(Level level, String message) {
        ConsoleHandler logger = new ConsoleHandler();
        LogRecord record = new LogRecord(level, message);
        logger.publish(record);
        logger.flush();
        logger.close();
    }

}
