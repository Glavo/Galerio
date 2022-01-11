package org.glavo.galerio;

import java.nio.file.Path;

public final class GalerioOptions {
    private final Path path;
    private final Path repoPath;
    private final boolean requiredLockForReading;

    public GalerioOptions(Path path, Path repoPath, boolean requiredLockForReading) {
        this.path = path;
        this.repoPath = repoPath;
        this.requiredLockForReading = requiredLockForReading;
    }

    public Path getPath() {
        return path;
    }

    public Path getRepoPath() {
        return repoPath;
    }

    public boolean isRequiredLockForReading() {
        return requiredLockForReading;
    }
}
