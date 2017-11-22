/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.store;

import edu.jhu.hlt.concrete.Communication;

public interface Store extends AutoCloseable {
  /**
   * Save a communication in the store
   * @param c Communication to save
   */
  void save(Communication c);
}