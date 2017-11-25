/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.manager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.stretcher.Server;
import edu.jhu.hlt.stretcher.file.FileUtility;
import edu.jhu.hlt.stretcher.file.FilenameMapper;
import edu.jhu.hlt.stretcher.file.FlatMapper;
import edu.jhu.hlt.stretcher.file.FormatDetector;
import edu.jhu.hlt.stretcher.file.GzConcreteFiles;
import edu.jhu.hlt.stretcher.source.CachingSource;
import edu.jhu.hlt.stretcher.source.Source;
import edu.jhu.hlt.stretcher.source.DirectorySource;
import edu.jhu.hlt.stretcher.source.FilteringSource;
import edu.jhu.hlt.stretcher.source.ZipSource;
import edu.jhu.hlt.stretcher.store.CacheUpdatingStore;
import edu.jhu.hlt.stretcher.store.CombiningStore;
import edu.jhu.hlt.stretcher.store.DirectoryStore;
import edu.jhu.hlt.stretcher.store.Store;
import edu.jhu.hlt.stretcher.store.ZipStore;
import edu.jhu.hlt.stretcher.util.DependencyLoader;

/**
 * Constructs the manager based on configuration and command line options.
 */
public class ManagerFactory {
  private static Logger LOGGER = LoggerFactory.getLogger(ManagerFactory.class);

  private static final String CONFIG = "stretcher.conf";

  private static FormatDetector detector;

  public static Manager create(Server.Opts opts) throws IOException {
    Config config = loadConfig();
    DependencyLoader loader = new DependencyLoader(config);
    CachingSource source = prepareSource(createSource(opts), loader);
    Store store = prepareStore(createStore(opts), source, loader);
    return loader.getManager(source, store);
  }

  private static Source createSource(Server.Opts opts) throws IOException {
    Source source = null;
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

  private static CachingSource prepareSource(Source source, DependencyLoader loader) {
    return new CachingSource(new FilteringSource(source, loader.getFilter()), loader.getCache());
  }

  private static Store createStore(Server.Opts opts) throws IOException {
    Store store = null;
    Path path = Paths.get(opts.outputPath);
    if (Files.isDirectory(path)) {
      FileUtility.validateDirectory(path);
      if (detector != null) {
        FilenameMapper mapper = new FlatMapper(path, detector.getExtension());
        store = new DirectoryStore(mapper, detector.getHelper());
      } else {
        // default to gz compressed
        store = new DirectoryStore(new FlatMapper(path, "gz"), new GzConcreteFiles());
      }
      LOGGER.info("Store running on the directory " + path.toString());
    } else {
      store = new ZipStore(path);
      LOGGER.info("Store is running on the zip file " + path.toString());
    }
    return store;
  }

  private static Store prepareStore(Store store, CachingSource source, DependencyLoader loader) {
    return new CombiningStore(new CacheUpdatingStore(store, source), loader.getCombiner());
  }

  private static Config loadConfig() {
    Config defaultConfig = ConfigFactory.load();
    Config config = defaultConfig;
    ClassLoader classLoader = ManagerFactory.class.getClassLoader();
    URL url = classLoader.getResource(CONFIG);
    if (url != null) {
      File configFile = new File(url.getFile());
      LOGGER.info("Loading configuration from " + configFile.getAbsolutePath());
      config = ConfigFactory.parseFile(configFile).withFallback(defaultConfig);
    }
    return config;
  }

}
