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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.stretcher.Server;
import edu.jhu.hlt.stretcher.fetch.CachingSource;
import edu.jhu.hlt.stretcher.fetch.CommunicationSource;
import edu.jhu.hlt.stretcher.fetch.DirectorySource;
import edu.jhu.hlt.stretcher.fetch.FilteringSource;
import edu.jhu.hlt.stretcher.fetch.ZipSource;
import edu.jhu.hlt.stretcher.file.FileUtility;
import edu.jhu.hlt.stretcher.file.FilenameMapper;
import edu.jhu.hlt.stretcher.file.FlatMapper;
import edu.jhu.hlt.stretcher.file.FormatDetector;
import edu.jhu.hlt.stretcher.file.GzConcreteFiles;
import edu.jhu.hlt.stretcher.store.CacheUpdatingPersister;
import edu.jhu.hlt.stretcher.store.CombiningPersister;
import edu.jhu.hlt.stretcher.store.DirectoryPersister;
import edu.jhu.hlt.stretcher.store.NoOpPersister;
import edu.jhu.hlt.stretcher.store.Persister;
import edu.jhu.hlt.stretcher.util.DependencyLoader;

/**
 * Constructs the manager based on configuration and command line options.
 */
public class ManagerFactory {
  private static Logger LOGGER = LoggerFactory.getLogger(ManagerFactory.class);

  private static FormatDetector detector;

  public static Manager create(Server.Opts opts) throws IOException {
    Config config = loadConfig();
    DependencyLoader loader = new DependencyLoader(config);
    CachingSource source = prepareSource(createSource(opts), loader);
    Persister persister = preparePersister(createPersister(opts), source, loader);
    return loader.getManager(source, persister);
  }

  private static CommunicationSource createSource(Server.Opts opts) throws IOException {
    CommunicationSource source = null;
    Path path = Paths.get(opts.inputPath);
    if (Files.isDirectory(path)) {
      FileUtility.validateDirectory(path);
      FormatDetector detector = new FormatDetector(path);
      source = new DirectorySource(path, detector.getMapper(), detector.getHelper());
      LOGGER.info("Fetch running on the directory " + path.toString());
    } else {
      source = new ZipSource(path);
      LOGGER.info("Fetch running on the zip file " + path.toString());
    }
    return source;
  }

  private static CachingSource prepareSource(CommunicationSource source, DependencyLoader loader) {
    return new CachingSource(new FilteringSource(source, loader.getFilter()), loader.getCache());
  }

  private static Persister createPersister(Server.Opts opts) throws IOException {
    Persister persister = null;
    Path path = Paths.get(opts.outputPath);
    if (Files.isDirectory(path)) {
      FileUtility.validateDirectory(path);
      if (detector != null) {
        FilenameMapper mapper = new FlatMapper(path, detector.getExtension());
        persister = new DirectoryPersister(mapper, detector.getHelper());
      } else {
        // default to gz compressed
        persister = new DirectoryPersister(new FlatMapper(path, "gz"), new GzConcreteFiles());
      }
      LOGGER.info("Store running on the directory " + path.toString());
    } else {
      persister = new NoOpPersister();
      LOGGER.info("Store is not running");
    }
    return persister;
  }

  private static Persister preparePersister(Persister persister, CachingSource source, DependencyLoader loader) {
    return new CombiningPersister(new CacheUpdatingPersister(persister, source), loader.getCombiner());
  }

  private static Config loadConfig() {
    Config defaultConfig = ConfigFactory.load();
    return defaultConfig;
  }

}
