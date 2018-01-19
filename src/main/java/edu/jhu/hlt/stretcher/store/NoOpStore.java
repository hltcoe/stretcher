/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;

public class NoOpStore implements Store {
  private static final Logger LOGGER = LoggerFactory.getLogger(NoOpStore.class);

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.storage.Store#save(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void save(Communication c) {
    LOGGER.info("Received for storing: " + c.getId());
  }

  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {}

}
