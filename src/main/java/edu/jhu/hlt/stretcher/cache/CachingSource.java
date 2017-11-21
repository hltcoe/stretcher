/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.cache;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.fetch.CommunicationSource;

/**
 * Interface for a wrapper around a source that provides caching.
 */
public interface CachingSource extends CommunicationSource {
  /**
   * Update the cache with a communication sent to the store service.
   * @param c The communication to update.
   */
  public void update(Communication c);
}
