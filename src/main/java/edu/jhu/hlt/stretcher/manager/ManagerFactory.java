/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.stretcher.Server;
import edu.jhu.hlt.stretcher.fetch.CommunicationSource;
import edu.jhu.hlt.stretcher.fetch.DirectorySource;
import edu.jhu.hlt.stretcher.fetch.ZipSource;
import edu.jhu.hlt.stretcher.store.DirectoryPersister;
import edu.jhu.hlt.stretcher.store.NoOpPersister;
import edu.jhu.hlt.stretcher.store.Persister;

/**
 * Constructs the manager based on configuration and command line options.
 */
public class ManagerFactory {
  private static Logger LOGGER = LoggerFactory.getLogger(ManagerFactory.class);

  public static Manager create(Server.Opts opts) throws IOException {
    Manager manager = null;

    Path path = Paths.get(opts.path);
    if (Files.isDirectory(path)) {
      CommunicationSource source = new DirectorySource(path);
      Persister persister = new DirectoryPersister(path);
      manager = new LockingManager(source, persister);
      LOGGER.info("Serving from the directory " + path.toString());
    } else {
      CommunicationSource source = new ZipSource(path);
      Persister persister = new NoOpPersister();
      manager = new LockingManager(source, persister);
      LOGGER.info("Serving from the zip file " + path.toString());
    }

    return manager;
  }
}
