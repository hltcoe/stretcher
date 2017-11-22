/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.filter;

import com.typesafe.config.Config;

import edu.jhu.hlt.concrete.Communication;

/**
 * Remove data from a communication that is not required.
 *
 * The constructor should not take any arguments.
 * Instead, the initialize() method is used to initialize the object.
 * The Config object is loaded from a configuration file.
 * @see DependencyLoader
 */
public interface Filter {
  /**
   * Initialize the cache
   * @param config Config object
   */
  public void initialize(Config config);

  /**
   * Slim down a communication
   * @param c The communication that needs a diet.
   */
  public void filter(Communication c);
}
