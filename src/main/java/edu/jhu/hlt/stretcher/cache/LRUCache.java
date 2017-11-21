/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.fetch.CommunicationSource;

public class LRUCache extends AbstractCachingSource {
  private static final Logger LOGGER = LoggerFactory.getLogger(LRUCache.class);

  private static final long DEFAULT_MAX_SIZE = 1000L;

  private final ConcurrentMap<String, Communication> cache;

  public LRUCache(CommunicationSource source) {
    this(source, DEFAULT_MAX_SIZE);
  }

  public LRUCache(CommunicationSource source, long size) {
    super(source);
    this.cache = CacheBuilder.newBuilder().maximumSize(size).<String, Communication>build().asMap();
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#exists(java.lang.String)
   */
  @Override
  public boolean exists(String id) {
    return cache.containsKey(id) || source.exists(id);
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#size()
   */
  @Override
  public int size() {
    return source.size();
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(java.lang.String)
   */
  @Override
  public Optional<Communication> get(String id) {
    Communication c = cache.get(id);
    if (c != null) {
      LOGGER.debug("Cache hit for " + id);
      return Optional.of(c);
    } else {
      Optional<Communication> opt = source.get(id);
      if (opt.isPresent()) {
        cache.put(id, opt.get());
      }
      return opt;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(java.util.List)
   */
  @Override
  public List<Communication> get(List<String> ids) {
    List<Communication> comms = new ArrayList<Communication>();
    for (String id : ids) {
      this.get(id).ifPresent(comms::add);
    }
    LOGGER.info("Returning " + comms.size() + " communications");
    return comms;
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(long, long)
   */
  @Override
  public List<Communication> get(long offset, long nToGet) {
    return source.get(offset, nToGet);
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.cache.CachingSource#update(edu.jhu.hlt.concrete.Communication)
   */
  public void update(Communication c) {
    cache.replace(c.getId(), c);
  }

}
