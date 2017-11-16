/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.MoreExecutors;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.stretcher.source.CommunicationSource;
import edu.jhu.hlt.stretcher.storage.Persister;

/**
 * Fetch and store communications from a directory.
 * Store overwrites the original communication files.
 */
public class SimpleFileEngine implements CommunicationSource, Persister {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFileEngine.class);

  private final Path directory;
  private final ConcreteFiles helper;
  private final ExecutorService executorService;
  private final FilenameMapper mapper;

  public SimpleFileEngine(Path directory) throws IOException {
    validateDirectory(directory);
    FormatDetector detector = new FormatDetector(directory);
    this.mapper = detector.getMapper();
    this.helper = detector.getHelper();
    this.executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
    this.directory = directory.toAbsolutePath();
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#exists(java.lang.String)
   */
  @Override
  public boolean exists(String id) {
    return Files.exists(mapper.map(id));
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#size()
   */
  @Override
  public int size() {
    AtomicInteger counter = new AtomicInteger(0);
    try {
      Files.newDirectoryStream(directory,
              path -> this.mapper.match(path))
              .forEach(path -> counter.getAndIncrement());
    } catch (IOException e) {
      LOGGER.error("Failed to count files in " + directory.toString(), e);
    }
    LOGGER.info("size() is returning " + counter.get());
    return counter.get();
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(java.lang.String)
   */
  @Override
  public Optional<Communication> get(String id) {
    return load(mapper.map(id));
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(java.util.List)
   */
  @Override
  public List<Communication> get(List<String> ids) {
    List<Communication> comms = new ArrayList<Communication>();
    for (String id : ids) {
      this.get(id).ifPresent(comms::add);
    }
    LOGGER.info("Returning " + comms.size() + " communications");
    return comms;
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(long, long)
   */
  @Override
  public List<Communication> get(long offset, long nToGet) {
    List<Communication> comms = Collections.emptyList();
    // TODO this assumes all files are communications in directory
    try {
      comms = Files.list(directory)
              .sorted()
              .skip(offset)
              .limit(nToGet)
              .map(this::load)
              .filter(c -> c.isPresent())
              .map(c -> c.get())
              .collect(Collectors.toList());
    } catch (IOException e) {
      LOGGER.warn("Unable to open " + directory.toString());
    }
    LOGGER.info("Returning " + comms.size() + " communications");
    return comms;
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.storage.Persister#store(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void store(Communication c) {
    this.executorService.submit(() -> {
      try {
        helper.write(mapper.map(c.getId()), c);
      } catch (ConcreteException e) {
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
    this.executorService.shutdown();
    LOGGER.info("Shutdown triggered; awaiting task termination");
    this.executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
  }

  private static void validateDirectory(Path directory) throws IOException {
    if (!Files.exists(directory)) {
      throw new IOException(directory.toString() + " does not exist");
    }
    try {
      Files.list(directory);
    } catch (IOException e) {
      throw new IOException("Cannot open " + directory.toString(), e);
    }
  }

  private Optional<Communication> load(Path path) {
    try {
      Communication comm = helper.read(path);
      return Optional.of(comm);
    } catch (ConcreteException e) {
      return Optional.empty();
    }
  }

}
