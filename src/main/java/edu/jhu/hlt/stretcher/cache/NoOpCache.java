/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.cache;

import com.typesafe.config.Config;

import edu.jhu.hlt.concrete.Communication;

/**
 * A cache that doesn't cache.
 */
public class NoOpCache implements Cache {

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.cache.Cache#initialize(com.typesafe.config.Config)
   */
  @Override
  public void initialize(Config config) {}

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.cache.Cache#exists(java.lang.String)
   */
  @Override
  public boolean exists(String id) {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.cache.Cache#get(java.lang.String)
   */
  @Override
  public Communication get(String id) {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.cache.Cache#put(java.lang.String, edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void put(String id, Communication c) {}

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.cache.Cache#replace(java.lang.String, edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void replace(String id, Communication c) {}
}
