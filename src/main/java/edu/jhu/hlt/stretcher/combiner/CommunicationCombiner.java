/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.combiner;

import edu.jhu.hlt.concrete.Communication;

/**
 * Combines two communication objects into a single object.
 *
 * The objects are instances of the same communication.
 * They may have different annotations that need to be combined/selected.
 */
public interface CommunicationCombiner {
  /**
   * Combine two communication objects.
   * @param c1 The primary object.
   * @param c2 The object to be added to the primary.
   * @return New communication
   */
  public Communication combine(Communication c1, Communication c2);
}
