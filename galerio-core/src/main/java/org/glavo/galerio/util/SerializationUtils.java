package org.glavo.galerio.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@SuppressWarnings("PointlessBitwiseExpression")
public class SerializationUtils {

    public static final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(new Jdk8Module())
            .build();

    /*
    public static void writeInt(OutputStream output, int value) throws IOException {
        output.write((value >>> 24) & 0xFF);
        output.write((value >>> 16) & 0xFF);
        output.write((value >>> 8) & 0xFF);
        output.write((value >>> 0) & 0xFF);
    }

    public static int readInt(InputStream input) throws IOException {
        int ch1 = input.read();
        int ch2 = input.read();
        int ch3 = input.read();
        int ch4 = input.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    public static void writeLong(OutputStream output, long value) throws IOException {
        output.write((byte) (value >>> 56));
        output.write((byte) (value >>> 48));
        output.write((byte) (value >>> 40));
        output.write((byte) (value >>> 32));
        output.write((byte) (value >>> 24));
        output.write((byte) (value >>> 16));
        output.write((byte) (value >>> 8));
        output.write((byte) (value >>> 0));
    }

    public static long readLong(InputStream input) throws IOException {
        int ch1 = input.read();
        int ch2 = input.read();
        int ch3 = input.read();
        int ch4 = input.read();
        int ch5 = input.read();
        int ch6 = input.read();
        int ch7 = input.read();
        int ch8 = input.read();

        if ((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) < 0) {
            throw new EOFException();
        }

        return (((long) ch1 << 56) +
                ((long) ch2 << 48) +
                ((long) ch3 << 40) +
                ((long) ch4 << 32) +
                ((long) ch5 << 24) +
                (ch6 << 16) +
                (ch7 << 8) +
                (ch8 << 0));
    }
     */

    public static void writeString(DataOutputStream output, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        output.writeInt(bytes.length);
        output.write(bytes);
    }

    public static String readString(DataInputStream input) throws IOException {
        int length = input.readInt();
        if (length < 0) {
            throw new IOException("Invalid string length: " + length);
        }
        byte[] bytes = new byte[length];
        if (input.read(bytes) != length) {
            throw new EOFException();
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void writeStringArray(DataOutputStream output, String[] value) throws IOException {
        output.write(value.length);
        for (String s : value) {
            writeString(output, s);
        }
    }

    public static String[] readStringArray(DataInputStream input) throws IOException {
        int length = input.readInt();
        if (length < 0) {
            throw new IOException("Invalid array length: " + length);
        }
        String[] res = new String[length];
        for (int i = 0; i < length; i++) {
            res[i] = readString(input);
        }
        return res;
    }

    public static void writeInstant(DataOutputStream output, Instant instant) throws IOException {
        output.writeLong(instant.getEpochSecond());
        output.writeInt(instant.getNano());
    }

    public static Instant readInstant(DataInputStream input) throws IOException {
        long seconds = input.readLong();
        int nanos = input.readInt();
        return Instant.ofEpochSecond(seconds, nanos);
    }
}
