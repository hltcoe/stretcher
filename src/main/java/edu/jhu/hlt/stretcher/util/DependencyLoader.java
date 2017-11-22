/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.util;

import java.lang.reflect.InvocationTargetException;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.stretcher.cache.Cache;
import edu.jhu.hlt.stretcher.combiner.Combiner;
import edu.jhu.hlt.stretcher.filter.Filter;
import edu.jhu.hlt.stretcher.manager.Manager;
import edu.jhu.hlt.stretcher.source.Source;
import edu.jhu.hlt.stretcher.store.Store;

/**
 * Loads dependencies based on the configuration.
 *
 * The configuration file needs two parameters per dependency: class name and params.
 * The params are passed as a Config object to the dependency's initialize() method.
 */
public class DependencyLoader {

  private static final String MANAGER_CLASS = "stretcher.manager.class";
  private static final String MANAGER_PARAMS = "stretcher.manager.params";
  private static final String MANAGER_PKG = "edu.jhu.hlt.stretcher.manager";
  private static final String CACHE_CLASS = "stretcher.cache.class";
  private static final String CACHE_PARAMS = "stretcher.cache.params";
  private static final String CACHE_PKG = "edu.jhu.hlt.stretcher.cache";
  private static final String COMBINER_CLASS = "stretcher.combiner.class";
  private static final String COMBINER_PARAMS = "stretcher.combiner.params";
  private static final String COMBINER_PKG = "edu.jhu.hlt.stretcher.combiner";
  private static final String FILTER_CLASS = "stretcher.filter.class";
  private static final String FILTER_PARAMS = "stretcher.filter.params";
  private static final String FILTER_PKG = "edu.jhu.hlt.stretcher.filter";

  private final Config config;

  public DependencyLoader(Config config) {
    this.config = config;
  }

  public Manager getManager(Source source, Store store) {
    String clazz = config.getString(MANAGER_CLASS);
    if (!clazz.contains(".")) {
      clazz = MANAGER_PKG + "." + clazz;
    }
    Manager manager = (Manager)load(clazz);
    manager.initialize(source, store, getConfig(MANAGER_PARAMS));
    return manager;
  }

  public Cache getCache() {
    String clazz = config.getString(CACHE_CLASS);
    if (!clazz.contains(".")) {
      clazz = CACHE_PKG + "." + clazz;
    }
    Cache cache = (Cache)load(clazz);
    cache.initialize(getConfig(CACHE_PARAMS));
    return cache;
  }

  public Combiner getCombiner() {
    String clazz = config.getString(COMBINER_CLASS);
    if (!clazz.contains(".")) {
      clazz = COMBINER_PKG + "." + clazz;
    }
    Combiner combiner = (Combiner)load(clazz);
    combiner.initialize(getConfig(COMBINER_PARAMS));
    return combiner;
  }

  public Filter getFilter() {
    String clazz = config.getString(FILTER_CLASS);
    if (!clazz.contains(".")) {
      clazz = FILTER_PKG + "." + clazz;
    }
    Filter filter = (Filter)load(clazz);
    filter.initialize(getConfig(FILTER_PARAMS));
    return filter;
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
