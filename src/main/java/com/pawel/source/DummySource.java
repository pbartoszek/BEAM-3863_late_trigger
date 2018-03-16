package com.pawel.source;

import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.UnboundedSource;
import org.apache.beam.sdk.options.PipelineOptions;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DummySource extends UnboundedSource<String, UnboundedSource.CheckpointMark> {

    private RecordGenerator generator;

    public DummySource() {

    }

    public DummySource(RecordGenerator generator) {
        this.generator = generator;
    }

    public List<DummySource> split(int desiredNumSplits, PipelineOptions options) throws Exception {

        return Arrays.asList(
                new DummySource(new RecordGenerator("AA")),
                new DummySource(new RecordGenerator("BB")),
                new DummySource(new RecordGenerator("CC")),
                new DummySource(new RecordGenerator("DD")),
                new DummySource(new RecordGenerator("EE")),
                new DummySource(new RecordGenerator("FF")),
                new DummySource(new RecordGenerator("GG")),
                new DummySource(new RecordGenerator("HH")),
                new DummySource(new RecordGenerator("II")),
                new DummySource(new RecordGenerator("JJ")),
                new DummySource(new RecordGenerator("KK")),
                new DummySource(new RecordGenerator("LL")),
                new DummySource(new RecordGenerator("MM")),
                new DummySource(new RecordGenerator("NN")),
                new DummySource(new RecordGenerator("OO")),
                new DummySource(new RecordGenerator("PP"))
        );
    }

    public UnboundedReader<String> createReader(PipelineOptions options, @Nullable CheckpointMark checkpointMark) throws IOException {
        return new DummySourceReader(this, generator);
    }

    public Coder getCheckpointMarkCoder() {
        return SerializableCoder.of(DummyChekpointMark.class);
    }

    @Override
    public Coder<String> getOutputCoder() {
        return StringUtf8Coder.of();
    }
}
