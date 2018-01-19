/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.util;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Loads a user defined configuration file with a fallback to the default.
 */
public class ConfigLoader {
  private static Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);
  private static final String CONFIG = "stretcher.conf";

  public static Config load() {
    Config defaultConfig = ConfigFactory.load();
    Config config = defaultConfig;
    ClassLoader classLoader = ConfigLoader.class.getClassLoader();
    URL url = classLoader.getResource(CONFIG);
    if (url != null) {
      File configFile = new File(url.getFile());
      LOGGER.info("Loading configuration from " + configFile.getAbsolutePath());
      config = ConfigFactory.parseFile(configFile).withFallback(defaultConfig);
    }
    return config;
  }
}
