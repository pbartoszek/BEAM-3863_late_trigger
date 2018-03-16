package com.pawel.source;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public class RecordGenerator implements Serializable {

    private final String key;
    private Deque<DummyRecord> queue = new ConcurrentLinkedDeque<>();

    private static final Logger LOG = LoggerFactory.getLogger(RecordGenerator.class);
    private Thread thread;
    private volatile boolean generateRecords;
    private Runnable runnable;

    public RecordGenerator(String key) {
        this.key = key;
        this.generateRecords = true;
    }

    public void run() {
        runnable = () -> {
            LOG.info("Starting generator for key {}", key);
            while (generateRecords) {
                Instant now = Instant.now().toDateTime().withMillisOfSecond(0).toInstant();

                queue.add(new DummyRecord(key, now.plus(Duration.millis(1))));
                queue.add(new DummyRecord(key, now.plus(Duration.millis(2))));
                queue.add(new DummyRecord(key, now.plus(Duration.millis(3))));
                queue.add(new DummyRecord(key, now.plus(Duration.millis(4))));
                queue.add(new DummyRecord(key, now.plus(Duration.millis(5))));
                sleepSec(10);
                queue.add(new DummyRecord(key, now));
            }
        };
        thread = new Thread(runnable);

        thread.start();
    }


    private void sleepSec(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    public DummyRecord getNextRecord() {
        return queue.poll();
    }

    public String getKey() {
        return key;
    }

    public void shutdown() {
        generateRecords = false;
    }
}
