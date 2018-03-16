### Generating events

I created a dummy unbounded source which sends events AA, BB,..., PP (16 distinct) keys.

Every `UnboundedSource.UnboundedReader` generates only a one key type.

Dummy source returns current system time as a watermark.


### What are the expected triggerings

A dummy source sends 5 records with current timestamp T1 then waits 10 seconds and sends the 6th record with timestamp T1 (as late data)
The first triggering should be after watermark passes the end of the 10 sec window. In the logs you should see the output like:

```
2018-03-16 14:09:10 INFO  com.pawel.Job:43 - Count: KV{KK, 5}. For window [2018-03-16T14:09:00.000Z..2018-03-16T14:09:10.000Z), Pane PaneInfo{isFirst=true, timing=ON_TIME, index=0, onTimeIndex=0}
```


Then the late trigger should fire after 5 seconds and emit a late pane(after processing time trigger with delay is used)

```
2018-03-16 14:09:16 INFO  com.pawel.Job:43 - Count: KV{KK, 6}. For window [2018-03-16T14:09:00.000Z..2018-03-16T14:09:10.000Z), Pane PaneInfo{timing=LATE, index=1, onTimeIndex=1}
```


### What are the actual triggerings
For most of the keys I get late firings but for some keys late trigger is missed and the correct count `Count: KV{<KEY>, 6}` is
only emitted at the end of allowed lateness.


### Analysing job output

It's useful to capture job's standard output to a file for analysis.

You can use the following command to see how many late firings there have been so far:

```
while true; do cat job.log | grep 6} | wc -l | xargs -I {} bash -c 'echo -n $(date) Total records {} mod 16=; echo "{}%16" | bc' |  tee -a job_triggers.log; sleep 1; done
```

`grep 6}` pattern is part of `2018-03-16 14:09:10 INFO  com.pawel.Job:43 - Count: KV{KK, 6}. For window ...` line log produced by the job.
I divide the number of total late firings (should be one for every key for every 10 second window - 16 in total per window) modulo 16.
If modulo reminder is not 0 for an tens' of second it means that late trigger for one of the key didn't fire.


In the `logs` folder I attached the logs from my testing. You could clearly see that `job_with_bug_triggers.log` has many more lines where mod 16 != 0


### Trying out the patch

Please rename `AfterDelayFromFirstElementStateMachine_Patch` class to `AfterDelayFromFirstElementStateMachine` and rerun the job.
You might need to do a mvn clean before.

With patch in place I got the expected late triggers and if you run the command from *Analysing job output* step you will see
the most of the time modulo reminder is 0.
