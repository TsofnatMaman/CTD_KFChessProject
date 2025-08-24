package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.lang.reflect.Field;

import static org.mockito.Mockito.verify;

class Slf4jAdapterTest {
    private Slf4jAdapter adapter;
    private Logger mockLogger;

    @BeforeEach
    void setUp() throws Exception {
        adapter = new Slf4jAdapter(Slf4jAdapter.class);
        mockLogger = Mockito.mock(Logger.class);

        Field field = Slf4jAdapter.class.getDeclaredField("logger");
        field.setAccessible(true);
        field.set(adapter, mockLogger);
    }

    @Test
    void debugForwardsToLogger() {
        String message = "debug";
        adapter.debug(message);
        verify(mockLogger).debug(message);
    }

    @Test
    void infoForwardsToLogger() {
        String message = "info";
        adapter.info(message);
        verify(mockLogger).info(message);
    }

    @Test
    void warnForwardsToLogger() {
        String message = "warn";
        adapter.warn(message);
        verify(mockLogger).warn(message);
    }

    @Test
    void errorForwardsToLogger() {
        String message = "error";
        Throwable t = new RuntimeException("boom");
        adapter.error(message, t);
        verify(mockLogger).error(message, t);
    }
}