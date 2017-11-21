/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.util;

import java.lang.reflect.InvocationTargetException;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.stretcher.fetch.CommunicationSource;
import edu.jhu.hlt.stretcher.manager.Manager;
import edu.jhu.hlt.stretcher.store.Persister;

public class DependencyLoader {

  private static final String MANAGER_CLASS = "stretcher.manager.class";
  private static final String MANAGER_PARAMS = "stretcher.manager.params";
  private static final String MANAGER_PKG = "edu.jhu.hlt.stretcher.manager";

  private final Config config;

  public DependencyLoader(Config config) {
    this.config = config;
  }

  public Manager getManager(CommunicationSource source, Persister persister) {
    String clazz = config.getString(MANAGER_CLASS);
    if (!clazz.contains(".")) {
      clazz = MANAGER_PKG + "." + clazz;
    }
    Manager manager = (Manager)load(clazz);
    manager.initialize(source, persister, getConfig(MANAGER_PARAMS));
    return manager;
  }

  private Object load(String clazz) {
    try {
      return Class.forName(clazz).getConstructors()[0].newInstance();
    } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
      throw new RuntimeException("Cannot construct " + clazz, ex);
    }
  }

  private Config getConfig(String key) {
    if (config.hasPath(key)) {
      return config.getConfig(key);
    } else {
      return ConfigFactory.empty();
    }
  }
}
