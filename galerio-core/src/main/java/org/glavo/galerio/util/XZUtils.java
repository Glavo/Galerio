package org.glavo.galerio.util;

import org.tukaani.xz.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class XZUtils {
    public static final ArrayCache DEFAULT_ARRAY_CACHE = new ResettableArrayCache(new ArrayCache());
    public static final int DEFAULT_CHECK_TYPE = XZ.CHECK_CRC64;

    public static XZInputStream newXZInputStream(InputStream input) throws IOException {
        return new XZInputStream(input, DEFAULT_ARRAY_CACHE);
    }

    public static XZOutputStream newXZOutputStream(OutputStream output) throws IOException {
        return new XZOutputStream(output, new LZMA2Options(), DEFAULT_CHECK_TYPE, DEFAULT_ARRAY_CACHE);
    }
}
