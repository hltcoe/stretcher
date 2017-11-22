/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.cache.Cache;

/**
 * Wrapper around a source that provides caching.
 */
public class CachingSource implements Source {
  private static final Logger LOGGER = LoggerFactory.getLogger(CachingSource.class);

  private final Source source;
  private final Cache cache;

  public CachingSource(Source source, Cache cache) {
    this.source = source;
    this.cache = cache;
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.Source#exists(java.lang.String)
   */
  @Override
  public boolean exists(String id) {
    return cache.exists(id) || source.exists(id);
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.Source#size()
   */
  @Override
  public int size() {
    return source.size();
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.Source#get(java.lang.String)
   */
  @Override
  public Optional<Communication> get(String id) {
    Communication c = cache.get(id);
    if (c != null) {
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
   * @see edu.jhu.hlt.stretcher.source.Source#get(java.util.List)
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
   * @see edu.jhu.hlt.stretcher.source.Source#get(long, long)
   */
  @Override
  public List<Communication> get(long offset, long nToGet) {
    return source.get(offset, nToGet);
  }

  /**
   * Update the cache based on a store request
   * @param c Communication
   */
  public void update(Communication c) {
    cache.replace(c.getId(), c);
  }
}
