package com.pawel.source;

import org.apache.beam.sdk.io.UnboundedSource;

import java.io.IOException;
import java.io.Serializable;

public class DummyChekpointMark implements UnboundedSource.CheckpointMark, Serializable {
    @Override
    public void finalizeCheckpoint() throws IOException {

    }
}
