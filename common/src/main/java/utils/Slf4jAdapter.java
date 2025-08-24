package utils;

import interfaces.AppLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jAdapter implements AppLogger {

    private final Logger logger;

    public Slf4jAdapter(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.error(message, t);
    }
}