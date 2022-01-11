package org.glavo.galerio.repository;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.glavo.galerio.util.SerializationUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;

public final class GalerioFileItem {
    private final GalerioIndexData index;
    private final String[] path;
    private final Instant creationTime;
    private final Instant lastModifiedTime;

    @JsonCreator
    public GalerioFileItem(
            @JsonProperty("index") GalerioIndexData index,
            @JsonProperty("path") String[] path,
            @JsonProperty("creationTime") Instant creationTime,
            @JsonProperty("lastModifiedTime") Instant lastModifiedTime
    ) {
        this.index = index;
        this.path = path;
        this.creationTime = creationTime;
        this.lastModifiedTime = lastModifiedTime;
    }

    public GalerioIndexData getIndex() {
        return index;
    }

    public String[] getPath() {
        return path;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public Instant getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void writeTo(DataOutputStream output) throws IOException {
        index.writeTo(output);
        SerializationUtils.writeStringArray(output, path);
        SerializationUtils.writeInstant(output, creationTime);
        SerializationUtils.writeInstant(output, lastModifiedTime);
    }

    public static GalerioFileItem readFrom(DataInputStream input) throws IOException {
        GalerioIndexData index = GalerioIndexData.readFrom(input);
        String[] path = SerializationUtils.readStringArray(input);
        Instant creationTime = SerializationUtils.readInstant(input);
        Instant lastModifiedTime = SerializationUtils.readInstant(input);
        return new GalerioFileItem(index, path, creationTime, lastModifiedTime);
    }
}
