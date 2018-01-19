/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.combiner;

import com.typesafe.config.Config;

import edu.jhu.hlt.concrete.Communication;

/**
 * Save only the annotations from latest store request
 */
public class OverwriteCombiner implements Combiner {

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.combiner.Combiner#initialize(com.typesafe.config.Config)
   */
  @Override
  public void initialize(Config config) {}

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.combiner.Combiner#combine(
   *        edu.jhu.hlt.concrete.Communication, edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public Communication combine(Communication c1, Communication c2) {
    return c2;
  }

}
