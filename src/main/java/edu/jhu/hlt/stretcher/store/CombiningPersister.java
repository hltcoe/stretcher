/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.store;

import java.util.HashMap;
import java.util.Map;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.combiner.CommunicationCombiner;

/**
 * Guaranteed combining of communications.
 *
 * Limitations include synchronization in store() and a cache of all communications.
 */
public class CombiningPersister implements Persister {

  private final Persister persister;
  private final CommunicationCombiner combiner;
  private final Map<String, Communication> cache = new HashMap<>();

  public CombiningPersister(Persister persister, CommunicationCombiner combiner) {
    this.persister = persister;
    this.combiner = combiner;
  }

  @Override
  public void store(Communication c) {
    String id = c.getId();
    synchronized(this){
      if (cache.containsKey(id)) {
        Communication orig = cache.get(id);
        c = combiner.combine(orig, c);
        cache.put(id, c);
      } else {
        cache.put(id, c);
      }
      persister.store(c);
    }
  }

  @Override
  public void close() throws Exception {
    persister.close();
  }

}
