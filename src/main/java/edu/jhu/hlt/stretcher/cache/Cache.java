/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.cache;

import com.typesafe.config.Config;

import edu.jhu.hlt.concrete.Communication;

/**
 * Cache interface
 *
 * The constructor should not take any arguments.
 * Instead, the initialize() method is used to initialize the object.
 * The Config object is loaded from a configuration file.
 * @see DependencyLoader
 */
public interface Cache {

  /**
   * Initialize the cache
   * @param config Config object
   */
  public void initialize(Config config);

  /**
   * Does this communication id exist in the cache?
   * @param id Communication id
   * @return boolean
   */
  public boolean exists(String id);

  /**
   * Get the communication from the cache
   * @param id Communication id
   * @return Communication or null
   */
  public Communication get(String id);

  /**
   * Put the communication in the cache
   * @param id Communication id
   * @param c Communication
   */
  public void put(String id, Communication c);

  /**
   * Replace the communication in the cache if it exists
   * @param id Communication id
   * @param c Communication
   */
  public void replace(String id, Communication c);
}
