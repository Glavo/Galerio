package org.glavo.galerio.util;

import org.jetbrains.annotations.NotNull;

public class GalerioThread extends Thread {
    final GalerioThreadContext context = new GalerioThreadContext();

    public GalerioThread() {
    }

    public GalerioThread(Runnable target) {
        super(target);
    }

    public GalerioThread(@NotNull String name) {
        super(name);
    }

    public GalerioThread(Runnable target, String name) {
        super(target, name);
    }
}
