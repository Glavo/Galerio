package org.glavo.galerio.util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public final class GalerioThreadContext {
    private static final ThreadLocal<GalerioThreadContext> threadLocalContext = ThreadLocal.withInitial(GalerioThreadContext::new);

    public static GalerioThreadContext current() {
        return threadLocalContext.get();
    }

    private final ByteBuffer inputBuffer = ByteBuffer.allocate(IOUtils.BUFFER_SIZE);
    private ByteBuffer outputBuffer;

    private final MessageDigest sha256Digest = MessageDigestUtils.getSHA256Instance();

    public ByteBuffer getInputBuffer() {
        return inputBuffer;
    }

    public ByteBuffer getOutputBuffer() {
        if (outputBuffer == null) {
            outputBuffer = ByteBuffer.allocate(IOUtils.BUFFER_SIZE);
        }
        return outputBuffer;
    }

    public MessageDigest getSHA256Digest() {
        return sha256Digest;
    }
}
