/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.fetch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.stretcher.file.ConcreteFiles;
import edu.jhu.hlt.stretcher.file.FilenameMapper;

/**
 * Load communications from a directory.
 *
 * Requires that all communications have the same filename structure.
 * Requires that all files in the directory are communications.
 */
public class DirectorySource implements CommunicationSource {
  private static final Logger LOGGER = LoggerFactory.getLogger(DirectorySource.class);

  private final Path directory;
  private final ConcreteFiles helper;
  private final FilenameMapper mapper;

  public DirectorySource(Path directory, FilenameMapper mapper, ConcreteFiles helper) throws IOException {
    this.mapper = mapper;
    this.helper = helper;
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
    // this assumes all files are communications in directory
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

  private Optional<Communication> load(Path path) {
    try {
      Communication comm = helper.read(path);
      return Optional.of(comm);
    } catch (ConcreteException e) {
      return Optional.empty();
    }
  }

}
