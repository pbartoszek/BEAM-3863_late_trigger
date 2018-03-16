package com.pawel.source;

import org.apache.beam.sdk.io.UnboundedSource;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.NoSuchElementException;

public class DummySourceReader extends UnboundedSource.UnboundedReader<String> {

    private static final Logger LOG = LoggerFactory.getLogger(DummySourceReader.class);

    private final DummySource dummySource;
    private final RecordGenerator recordGenerator;

    private DummyRecord currentRecord;

    public DummySourceReader(DummySource dummySource, RecordGenerator recordGenerator) {
        this.dummySource = dummySource;
        this.recordGenerator = recordGenerator;
    }

    @Override
    public boolean start() throws IOException {
        LOG.info("Starting reader for key {}", recordGenerator.getKey());
        recordGenerator.run();
        return false;
    }

    @Override
    public boolean advance() throws IOException {
        DummyRecord newRecord = recordGenerator.getNextRecord();
        if (newRecord != null) {
            currentRecord = newRecord;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCurrent() throws NoSuchElementException {
        LOG.info("Read: " + currentRecord);
        return currentRecord.getBody();
    }

    @Override
    public Instant getCurrentTimestamp() throws NoSuchElementException {
        return currentRecord.getCreated();
    }

    @Override
    public void close() throws IOException {
        LOG.info("Closing reader for key {}", recordGenerator.getKey());
        recordGenerator.shutdown();
    }

    @Override
    public Instant getWatermark() {
        return new Instant();
    }

    @Override
    public UnboundedSource.CheckpointMark getCheckpointMark() {
        return new DummyChekpointMark();
    }

    @Override
    public UnboundedSource<String, ?> getCurrentSource() {
        return dummySource;
    }

}