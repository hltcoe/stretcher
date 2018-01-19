/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.store;

import java.util.HashMap;
import java.util.Map;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.combiner.Combiner;

/**
 * Guaranteed combining of communications.
 *
 * Limitations include synchronization in save() and a cache of all communications.
 */
public class CombiningStore implements Store {

  private final Store store;
  private final Combiner combiner;
  private final Map<String, Communication> cache = new HashMap<>();

  public CombiningStore(Store store, Combiner combiner) {
    this.store = store;
    this.combiner = combiner;
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.storage.Store#save(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void save(Communication c) {
    String id = c.getId();
    synchronized(this){
      if (cache.containsKey(id)) {
        Communication orig = cache.get(id);
        c = combiner.combine(orig, c);
        cache.put(id, c);
      } else {
        cache.put(id, c);
      }
      store.save(c);
    }
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
