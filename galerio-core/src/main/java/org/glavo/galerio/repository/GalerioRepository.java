package org.glavo.galerio.repository;

import org.glavo.galerio.Galerio;
import org.glavo.galerio.util.CollectionUtils;
import org.glavo.galerio.util.GalerioThreadContext;
import org.glavo.galerio.util.HexUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public final class GalerioRepository implements AutoCloseable {
    private final Galerio galerio;
    private FileLock lock;

    public GalerioRepository(Galerio galerio) {
        this.galerio = galerio;
    }

    public Path getPath() {
        return galerio.getRepoPath();
    }

    private Path lockFile() {
        return getPath().resolve("galerio.lock");
    }

    public boolean tryLock() {
        Path lockFile = lockFile();
        FileChannel channel = null;
        try {
            channel = FileChannel.open(lockFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            lock = channel.tryLock();
            if (lock == null) {
                channel.close();
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        }
    }

    public void unlock() {
        if (lock != null) {
            try {
                lock.release();
                lock.channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock = null;
            }
        }
    }

    private Path poolDir() {
        return getPath().resolve("pool");
    }

    public @Nullable Path getDataFilePath(GalerioIndexData index) {
        if (index.isInlined()) {
            return null;
        }

        return poolDir()
                .resolve(HexUtils.toHexString((byte) (index.getFileSize() % 256)))
                .resolve(HexUtils.toHexString(index.getFileHashOrData()));
    }


    @Override
    public void close() throws Exception {
        unlock();
    }
}
