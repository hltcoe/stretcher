/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.store;

import java.io.IOException;
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

/**
 * Thread-safe persister.
 *
 * Saves the communications in a directory.
 * There is a single worker thread that reads from a queue.
 * This overwrites an existing file when store is called.
 */
public class DirectoryPersister implements Persister {
  private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryPersister.class);

  private final ExecutorService executorService;
  private final ConcreteFiles helper;
  private final FilenameMapper mapper;

  public DirectoryPersister(FilenameMapper mapper, ConcreteFiles helper) throws IOException {
    this.mapper = mapper;
    this.helper = helper;
    this.executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
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
        LOGGER.debug("Saving " + c.getId());
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

}
