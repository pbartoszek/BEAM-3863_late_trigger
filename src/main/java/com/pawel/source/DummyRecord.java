package com.pawel.source;

import org.joda.time.Instant;

import java.util.UUID;

public class DummyRecord {
    private final Instant created;
    private final String id;
    private final String body;

    public DummyRecord(String body, Instant created) {
        this.created = created;
        this.body = body;
        this.id = UUID.randomUUID().toString();
    }

    public String getBody() {
        return body;
    }

    public Instant getCreated() {
        return created;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "DummyRecord{" +
                "body='" + body + '\'' +
                ", created=" + created +
                ", id='" + id + '\'' +
                '}';
    }
}
