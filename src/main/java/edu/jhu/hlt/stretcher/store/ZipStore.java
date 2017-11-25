/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.store;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.MoreExecutors;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.stretcher.file.ConcreteFiles;
import edu.jhu.hlt.stretcher.file.FilenameMapper;
import edu.jhu.hlt.stretcher.file.FlatMapper;
import edu.jhu.hlt.stretcher.file.UncompressedConcreteFiles;

/**
 * Store communications in a zip archive.
 *
 * Uses filenames of the form [id].comm.
 * Uses a flat directory structure.
 */
public class ZipStore implements Store {
  private static final Logger LOGGER = LoggerFactory.getLogger(ZipStore.class);

  private static final String EXTENSION = "comm";

  private final ExecutorService executorService;
  private final FileSystem fs;
  private final Path tmpDir;
  private final ConcreteFiles helper;
  private final FilenameMapper mapper;

  public ZipStore(Path path) throws IOException {
    this.executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
    this.fs = createZipFileSystem(path);
    this.tmpDir = Files.createTempDirectory("stretecher");
    this.helper = new UncompressedConcreteFiles();
    this.mapper = new FlatMapper(Paths.get(""), EXTENSION);
  }

  private FileSystem createZipFileSystem(Path path) throws IOException {
    Map<String, String> env = new HashMap<>();
    env.put("create", String.valueOf(!Files.exists(path)));
    env.put("encoding", "UTF-8");
    URI uri = URI.create("jar:file:" + path.toAbsolutePath());
    return FileSystems.newFileSystem(uri, env);
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.storage.Store#save(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void save(Communication c) {
    this.executorService.submit(() -> {
      try {
        String filename = mapper.map(c.getId()).toString();
        Path onDiskPath = tmpDir.resolve(filename);
        Path inZipPath = fs.getPath(filename);
        if (Files.exists(inZipPath)) {
          Files.delete(inZipPath);
        }
        helper.write(onDiskPath, c);
        Files.copy(onDiskPath, inZipPath);
        Files.delete(onDiskPath);
        LOGGER.debug("Saving " + c.getId());
      } catch (ConcreteException | IOException e) {
        LOGGER.warn("Unable to store " + c.getId(), e);
      }
    });
  }

  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {
    executorService.shutdown();
    LOGGER.info("Shutdown triggered; awaiting task termination");
    executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    fs.close();
    if (Files.exists(tmpDir)) {
      Files.delete(tmpDir);
    }
  }
}
