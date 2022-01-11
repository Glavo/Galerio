package org.glavo.galerio;

import java.nio.file.Path;

public final class Galerio {
    private final GalerioOptions options;

    public Galerio(GalerioOptions options) {
        this.options = options;
    }

    public GalerioOptions getOptions() {
        return options;
    }

    public Path getRepoPath() {
        return options.getRepoPath();
    }
}
