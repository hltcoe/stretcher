/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.combiner;

import edu.jhu.hlt.concrete.Communication;

/**
 * Save only the annotations from c2
 */
public class OverwriteCombiner implements CommunicationCombiner {

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.combiner.CommunicationCombiner#combine(
   *        edu.jhu.hlt.concrete.Communication, edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public Communication combine(Communication c1, Communication c2) {
    return c2;
  }

}
