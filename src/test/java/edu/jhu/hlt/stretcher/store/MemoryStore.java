/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.store;

import java.util.HashMap;
import java.util.Map;

import edu.jhu.hlt.concrete.Communication;

public class MemoryStore implements Store {

  private final Map<String, Communication> store = new HashMap<>();

  public Communication retrieve(String id) {
    return store.get(id);
  }

  @Override
  public void save(Communication c) {
    store.put(c.getId(), c);
  }

  @Override
  public void close() throws Exception {}

}
