package org.glavo.galerio.util;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class GalerioThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(@NotNull Runnable r) {
        return new GalerioThread(r);
    }
}
