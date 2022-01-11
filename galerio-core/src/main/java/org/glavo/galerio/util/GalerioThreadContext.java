package org.glavo.galerio.util;

import java.security.MessageDigest;

public final class GalerioThreadContext {
    // private static final ThreadLocal<GalerioThreadContext> threadLocalContext = ThreadLocal.withInitial(GalerioThreadContext::new);

    public static GalerioThreadContext current() {
        // return threadLocalContext.get();
        return ((GalerioThread) Thread.currentThread()).context;
    }

    private final byte[] inputBuffer = new byte[FileUtils.BUFFER_SIZE];
    private byte[] outputBuffer;

    private final MessageDigest sha256Digest = MessageDigestUtils.getSHA256Instance();

    public byte[] getInputBuffer() {
        return inputBuffer;
    }

    public byte[] getOutputBuffer() {
        if (outputBuffer == null) {
            outputBuffer = new byte[FileUtils.BUFFER_SIZE];
        }
        return outputBuffer;
    }

    public MessageDigest getSHA256Digest() {
        return sha256Digest;
    }
}
