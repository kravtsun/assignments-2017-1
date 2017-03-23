package ru.spbau.mit;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class LoggerTester {
    private static final Logger LOG = LogManager.getLogger(LoggerTester.class);

    public static void main(String[] args) {
        LOG.trace("starting main");

        int one = 1;
        int zero = 0;

        try {
            int infinity = one / zero;
        } catch (java.lang.ArithmeticException e) {
            LOG.error("ArithmeticException");
        }

        LOG.debug("Debug");

        LOG.info("Info message");

        LOG.warn("Rain is coming");

        LOG.fatal("I can't live anymore! Exiting...");
    }
}
