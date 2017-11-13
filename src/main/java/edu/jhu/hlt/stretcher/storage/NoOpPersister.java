/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;

public class NoOpPersister implements Persister {
  private static final Logger LOGGER = LoggerFactory.getLogger(NoOpPersister.class);

  @Override
  public void store(Communication c) {
    LOGGER.info("Received for storing: " + c.getId());
  }

  @Override
  public void close() throws Exception {}

}
