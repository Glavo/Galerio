package org.glavo.galerio.repository;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.io.IOUtils;
import org.glavo.galerio.util.HexUtils;
import org.glavo.galerio.util.SerializationUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * 用于在池中索引文件内容。
 * <p>
 * 当文件大小小于等于 32 字节时，将文件内容内联存储在 {@link #fileHashOrData} 中；
 * 当文件大小大于 32 字节时，文件内容存储在存储池中。
 */
@JsonIncludeProperties({"fileSize", "fileHashOrData"})
public final class GalerioIndexData {
    private final long fileSize;
    private final byte[] fileHashOrData; // size == 32

    @JsonCreator
    public GalerioIndexData(
            @JsonProperty("fileSize") long fileSize,
            @JsonProperty("fileHashOrData") byte[] fileHashOrData) {
        assert fileHashOrData.length == Long.min(fileSize, 32);

        this.fileSize = fileSize;
        this.fileHashOrData = fileHashOrData;
    }

    public long getFileSize() {
        return fileSize;
    }

    public byte[] getFileHashOrData() {
        return fileHashOrData;
    }

    public String getFileHashAsString() {
        assert !isInlined();
        return HexUtils.toHexString(fileHashOrData);
    }

    public boolean isInlined() {
        return fileSize <= 32;
    }

    public String getPoolName() {
        return HexUtils.toHexString((byte) (fileSize % 256));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GalerioIndexData)) {
            return false;
        }
        GalerioIndexData that = (GalerioIndexData) o;
        return fileSize == that.fileSize && Arrays.equals(fileHashOrData, that.fileHashOrData);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fileSize);
        result = 31 * result + Arrays.hashCode(fileHashOrData);
        return result;
    }

    @Override
    public String toString() {
        return String.format("GalerioIndexData[fileSize=%d, fileHashOrData=%s]", fileSize, Arrays.toString(fileHashOrData));
    }

    public void writeTo(DataOutputStream output) throws IOException {
        output.writeLong(fileSize);
        output.write(fileHashOrData);
    }

    public static GalerioIndexData readFrom(DataInputStream input) throws IOException {
        long fileSize = input.readLong();
        if (fileSize < 0) {
            throw new IOException();
        }

        byte[] fileHashOrData = new byte[(int) Long.min(fileSize, 32)];
        if (input.read(fileHashOrData) != fileHashOrData.length) {
            throw new IOException();
        }

        return new GalerioIndexData(fileSize, fileHashOrData);
    }
}
