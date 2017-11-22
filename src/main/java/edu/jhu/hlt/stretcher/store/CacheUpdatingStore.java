/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.store;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.source.CachingSource;

/**
 * Updates the source cache on calls to save().
 */
public class CacheUpdatingStore implements Store {

  private final Store store;
  private final CachingSource source;

  public CacheUpdatingStore(Store store, CachingSource source) {
    this.store = store;
    if (source instanceof CachingSource) {
      this.source = (CachingSource)source;
    } else {
      this.source = null;
    }
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.storage.Store#save(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void save(Communication c) {
    if (source != null) {
      source.update(c);
    }
    store.save(c);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {
    store.close();
  }

}
