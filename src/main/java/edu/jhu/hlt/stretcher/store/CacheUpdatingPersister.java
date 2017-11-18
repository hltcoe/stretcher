/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.store;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.fetch.CachingSource;
import edu.jhu.hlt.stretcher.fetch.CommunicationSource;

/**
 * Updates the source cache on calls to store.
 */
public class CacheUpdatingPersister implements Persister {

  private final Persister persister;
  private final CachingSource source;

  public CacheUpdatingPersister(Persister persister, CommunicationSource source) {
    this.persister = persister;
    if (source instanceof CachingSource) {
      this.source = (CachingSource)source;
    } else {
      this.source = null;
    }
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.storage.Persister#store(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void store(Communication c) {
    if (source != null) {
      source.update(c);
    }
    persister.store(c);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {
    persister.close();
  }

}
