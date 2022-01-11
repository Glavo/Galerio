package org.glavo.galerio.repository;

import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FrameOutputStream;
import org.glavo.galerio.Galerio;
import org.glavo.galerio.util.GalerioThreadContext;
import org.glavo.galerio.util.FileUtils;
import org.glavo.galerio.util.XZUtils;
import org.tukaani.xz.XZOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public final class GalerioRepository implements AutoCloseable {
    private final Galerio galerio;
    private FileLock lock;

    private final Pool pool = new Pool();

    public GalerioRepository(Galerio galerio) {
        this.galerio = galerio;
    }

    public Path rootDir() {
        return galerio.getRepoPath();
    }

    private Path lockFile() {
        return rootDir().resolve("galerio.lock");
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

    public void unlock() throws IOException {
        if (lock != null) {
            try {
                lock.release();
                lock.channel().close();
            } finally {
                lock = null;
            }
        }
    }

    @Override
    public void close() throws Exception {
        unlock();
    }

    public GalerioRepository.Pool getPool() {
        return pool;
    }

    public enum PoolItemFormat {
        RAW((byte) 0),
        LZ4((byte) 1),
        XZ((byte) 2);

        public static final byte RAW_MARK = RAW.mark;
        public static final byte LZ4_MARK = LZ4.mark;
        public static final byte XZ_MARK = XZ.mark;

        public static PoolItemFormat getFormat(byte mark) {
            switch (mark) {
                case 0:
                    return RAW;
                case 1:
                    return LZ4;
                case 2:
                    return XZ;
            }
            return null;
        }

        private final byte mark;
        private final int headerLength;

        PoolItemFormat(byte mark) {
            this.mark = mark;
            this.headerLength = 1;
        }

        public byte getMark() {
            return mark;
        }

    }

    /**
     * 存储池，用于存储文件实际内容。
     * <p>
     * 文件内容由文件大小和文件 SHA-256 哈希值一同定位。
     */
    public final class Pool {
        private final SecureRandom random = new SecureRandom();
        private final ConcurrentHashMap<GalerioIndexData, Boolean> record = new ConcurrentHashMap<>();

        Pool() {
        }

        private Path poolDir() {
            return rootDir().resolve("pool");
        }

        public Path getDataFilePath(GalerioIndexData index) {
            if (index.isInlined()) {
                return null;
            }

            return poolDir()
                    .resolve(index.getPoolName())
                    .resolve(index.getFileHashAsString());
        }

        public InputStream newDataInputStream(GalerioIndexData index) throws IOException {
            Path path = getDataFilePath(index);
            if (path == null) { // Inlined
                return new ByteArrayInputStream(index.getFileHashOrData());
            }

            return Files.newInputStream(path);
        }

        private Path createTempFile() throws IOException {
            Path tempDir = poolDir().resolve("temp");
            Files.createDirectories(tempDir);

            return tempDir.resolve("random-" + random.nextLong() + ".tmp");
        }

        public void submit(GalerioIndexData index, byte[] data, PoolItemFormat format) throws IOException {
            Path path = getDataFilePath(index);
            if (path == null) { // Inlined
                return;
            }
            if (Files.exists(path)) {
                return; // The file is already in the pool
            }

            if (record.putIfAbsent(index, Boolean.TRUE) != null) {
                return; // The same file is being committed in another thread
            }

            Path tmpFile = createTempFile();

            try (OutputStream out = Files.newOutputStream(tmpFile)) {
                if (format == PoolItemFormat.RAW) {
                    out.write(PoolItemFormat.RAW_MARK);
                    out.write(data);
                } else if (format == PoolItemFormat.LZ4) {
                    if (data.length <= FileUtils.BUFFER_SIZE) {
                        byte[] outputBuffer = GalerioThreadContext.current().getOutputBuffer();
                        int compressedSize = Integer.MAX_VALUE;
                        try {
                            compressedSize = LZ4Factory.fastestJavaInstance().fastCompressor().compress(data, outputBuffer);
                        } catch (LZ4Exception ignored) {
                        }

                        if (compressedSize < data.length) {
                            out.write(PoolItemFormat.LZ4_MARK);
                            out.write(outputBuffer, 0, compressedSize);
                        } else {
                            // Compression ratio higher than 100%, replace with write raw data
                            out.write(PoolItemFormat.RAW_MARK);
                            out.write(data);
                        }
                    } else {
                        out.write(PoolItemFormat.LZ4_MARK);
                        try (LZ4FrameOutputStream lz4 = new LZ4FrameOutputStream(out)) {
                            lz4.write(data);
                        }
                    }
                } else if (format == PoolItemFormat.XZ) {
                    out.write(PoolItemFormat.XZ_MARK);
                    try (XZOutputStream xz = XZUtils.newXZOutputStream(out)) {
                        xz.write(data);
                    }
                } else {
                    throw new AssertionError("Unsupported file format: " + format);
                }
            }

            Files.move(tmpFile, path);
        }
    }

    /**
     * 相册，用于存放快照。
     */
    public final class Album {
        Album() {
        }

        private Path AlbumDir() {
            return rootDir().resolve("album");
        }


    }


}
