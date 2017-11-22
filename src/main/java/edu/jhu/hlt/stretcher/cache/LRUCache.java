/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.cache;

import java.util.concurrent.ConcurrentMap;

import com.google.common.cache.CacheBuilder;
import com.typesafe.config.Config;

import edu.jhu.hlt.concrete.Communication;

/**
 * Thread-safe least recently used memory cache.
 *
 * Config parameters:
 *  - size
 */
public class LRUCache implements Cache {

  private static final long DEFAULT_MAX_SIZE = 1000L;

  private ConcurrentMap<String, Communication> cache;

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.cache.Cache#initialize(com.typesafe.config.Config)
   */
  @Override
  public void initialize(Config config) {
    long size = DEFAULT_MAX_SIZE;
    if (config.hasPath("size")) {
      size = config.getLong("size");
    }
    this.cache = CacheBuilder.newBuilder().maximumSize(size).<String, Communication>build().asMap();
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.cache.Cache#exists(java.lang.String)
   */
  @Override
  public boolean exists(String id) {
    return cache.containsKey(id);
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.cache.Cache#get(java.lang.String)
   */
  @Override
  public Communication get(String id) {
    return cache.get(id);
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.cache.Cache#put(java.lang.String, edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void put(String id, Communication c) {
    cache.put(id, c);
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.cache.Cache#replace(java.lang.String, edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void replace(String id, Communication c) {
    cache.replace(id, c);
  }
}
