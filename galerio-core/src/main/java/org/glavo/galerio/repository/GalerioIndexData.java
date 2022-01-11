package org.glavo.galerio.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * 用于在池中索引文件内容。
 * <p>
 * 当文件大小小于等于 32 字节时，将文件内容内联存储在 {@link #fileHashOrData} 中；
 * 当文件大小大于 32 字节时，文件内容存储在存储池中。
 */
@SuppressWarnings("PointlessBitwiseExpression")
public final class GalerioIndexData {
    private final long fileSize;
    private final byte[] fileHashOrData; // size == 32

    public GalerioIndexData(long fileSize, byte[] fileHashOrData) {
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

    public boolean isInlined() {
        return fileSize <= 32;
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

    public void writeTo(OutputStream output) throws IOException {
        // Write File Size
        output.write((byte) (fileSize >>> 56));
        output.write((byte) (fileSize >>> 48));
        output.write((byte) (fileSize >>> 40));
        output.write((byte) (fileSize >>> 32));
        output.write((byte) (fileSize >>> 24));
        output.write((byte) (fileSize >>> 16));
        output.write((byte) (fileSize >>> 8));
        output.write((byte) (fileSize >>> 0));

        output.write(fileHashOrData);
    }

    public static GalerioIndexData readFrom(InputStream input) throws IOException {
        long fileSize = (((long) input.read() << 56) +
                ((long) (input.read() & 255) << 48) +
                ((long) (input.read() & 255) << 40) +
                ((long) (input.read() & 255) << 32) +
                ((long) (input.read() & 255) << 24) +
                ((input.read() & 255) << 16) +
                ((input.read() & 255) << 8) +
                ((input.read() & 255) << 0));

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
