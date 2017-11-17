/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.filter;

import edu.jhu.hlt.concrete.Communication;

/**
 * Remove data from a communication that is not required.
 */
public interface CommunicationFilter {
  /**
   * Slim down a communication
   * @param c The communication that needs a diet.
   */
  public void filter(Communication c);
}
