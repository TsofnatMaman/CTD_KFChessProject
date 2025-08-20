package events;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple concurrency/stress test for EventPublisher.
 */
class EventPublisherConcurrencyTest {

    @Test
    void concurrentPublishAndSubscribe_noExceptions() throws Exception {
        EventPublisher pub = EventPublisher.getInstance();

        final int threads = 8;
        final int publishesPerThread = 200;
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        AtomicInteger received = new AtomicInteger(0);

        IEventListener listener = event -> received.incrementAndGet();

        // subscribe once
        pub.subscribe(EGameEvent.GAME_UPDATE, listener);

        CountDownLatch latch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            ex.submit(() -> {
                for (int j = 0; j < publishesPerThread; j++) {
                    try {
                        pub.publish(EGameEvent.GAME_UPDATE, new GameEvent(EGameEvent.GAME_UPDATE, null));
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail("Exception during publish: " + e.getMessage());
                    }
                }
                latch.countDown();
            });
        }

        boolean ok = latch.await(10, TimeUnit.SECONDS);
        assertTrue(ok, "publish tasks finished in time");

        // give small time for listeners to run
        Thread.sleep(200);

        assertTrue(received.get() >= threads * publishesPerThread);
        ex.shutdownNow();
        pub.unsubscribe(EGameEvent.GAME_UPDATE, listener);
    }
}
