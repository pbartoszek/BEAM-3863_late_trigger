package com.pawel;

import com.pawel.source.DummySource;
import org.apache.beam.runners.flink.FlinkPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.windowing.*;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class Job implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(Job.class);


    public static void main(String[] args) {

        String[] runnerArgs = {"--runner=FlinkRunner", "--parallelism=8"};

        FlinkPipelineOptions options = PipelineOptionsFactory.fromArgs(runnerArgs).as(FlinkPipelineOptions.class);
        Pipeline pipeline = Pipeline.create(options);
        PCollection<String> apply = pipeline.apply(Read.from(new DummySource()))
                .apply(Window.<String>into(FixedWindows.of(Duration.standardSeconds(10)))
                        .triggering(AfterWatermark.pastEndOfWindow()
                                .withLateFirings(
                                        AfterProcessingTime
                                                .pastFirstElementInPane().plusDelayOf(Duration.standardSeconds(5))))
                        .accumulatingFiredPanes()
                        .withAllowedLateness(Duration.standardMinutes(2), Window.ClosingBehavior.FIRE_IF_NON_EMPTY)
                );
        apply.apply(Count.perElement())
                .apply(ParDo.of(new DoFn<KV<String, Long>, Long>() {
                    @ProcessElement
                    public void process(ProcessContext context, BoundedWindow window) {
                        LOG.info("Count: {}. For window {}, Pane {}", context.element(), window, context.pane());
                    }
                }));

        pipeline.run().waitUntilFinish();
    }
}
