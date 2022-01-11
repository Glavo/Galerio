package org.glavo.galerio.repository;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public final class GalerioSnapshot {
    private final Instant timestamp;
    private final String name;
    private final List<GalerioFileItem> items;

    @JsonCreator
    public GalerioSnapshot(
            @JsonProperty("timestamp") Instant timestamp,
            @JsonProperty("name") String name,
            @JsonProperty("items") List<GalerioFileItem> items
    ) {
        this.timestamp = timestamp;
        this.name = name;
        this.items = items;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public List<GalerioFileItem> getItems() {
        return items;
    }
}
