/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.jhu.hlt.concrete.Communication;

public class MemorySource implements Source {

  private final Map<String, Communication> map;

  public MemorySource(Map<String, Communication> map) {
    this.map = map;
  }

  @Override
  public boolean exists(String id) {
    return map.containsKey(id);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public Optional<Communication> get(String id) {
    return Optional.ofNullable(map.get(id));
  }

  @Override
  public List<Communication> get(List<String> ids) {
    List<Communication> comms = new ArrayList<Communication>();
    for (String id : ids) {
      this.get(id).ifPresent(comms::add);
    }
    return comms;
  }

  @Override
  public List<Communication> get(long offset, long nToGet) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void close() throws Exception {}

}
