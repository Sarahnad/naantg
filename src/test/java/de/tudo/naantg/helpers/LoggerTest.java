package de.tudo.naantg.helpers;

import org.junit.jupiter.api.Test;

public class LoggerTest {

    @Test
    public void testLog() {
        String warning = "This is a warning!";
        String info = "This is an info!";

        Logger.logWarning(warning);
        Logger.logInfo(info);
    }

}