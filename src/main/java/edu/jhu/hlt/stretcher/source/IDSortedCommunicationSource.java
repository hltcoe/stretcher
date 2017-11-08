/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.source;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

import com.google.common.collect.ImmutableList;

import edu.jhu.hlt.concrete.Communication;

/**
 *
 */
public class IDSortedCommunicationSource implements CommunicationSource {

  private final NavigableMap<String, Communication> impl;

  public IDSortedCommunicationSource(NavigableMap<String, Communication> impl) {
    this.impl = impl;
  }

  public IDSortedCommunicationSource(Iterable<Communication> comms) {
    NavigableMap<String, Communication> tm = new TreeMap<>();
    comms.forEach(c -> tm.put(c.getId(), c));
    this.impl = tm;
  }

  public IDSortedCommunicationSource() {
    this(new TreeMap<>());
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(java.lang.String)
   */
  @Override
  public Optional<Communication> get(String id) {
    if (!impl.containsKey(id))
      return Optional.empty();
    return Optional.of(this.impl.get(id));
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(long, long)
   */
  @Override
  public List<Communication> get(long offset, long nToGet) {
    // return copies, not refs
    ImmutableList.Builder<Communication> bldr = ImmutableList.builder();
    // ordered iterator
    Iterator<Map.Entry<String, Communication>> iter = this.impl.entrySet().iterator();
    // move to offset
    for (int i = 0; i < offset; i++) {
      if (iter.hasNext())
        iter.next();
      else
        // just return empty
        return bldr.build();
    }

    // iter now at position offset
    for (int i = 0; i < nToGet; i++) {
      if (iter.hasNext()) {
        bldr.add(iter.next().getValue());
      } else {
        // return whatever has been collected
        return bldr.build();
      }
    }
    return bldr.build();
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(java.util.List)
   */
  @Override
  public List<Communication> get(List<String> ids) {
    // return copies, not refs
    ImmutableList.Builder<Communication> bldr = ImmutableList.builder();
    for (String id : ids) {
      this.get(id).ifPresent(bldr::add);
    }
    return bldr.build();
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#exists(java.lang.String)
   */
  @Override
  public boolean exists(String id) {
    return this.impl.containsKey(id);
  }

  @Override
  public int size() {
    return this.impl.size();
  }
}
