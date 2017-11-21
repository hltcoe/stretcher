/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.cache;

import java.util.Optional;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.fetch.CommunicationSource;

/**
 * Pass through that does not implement any caching.
 */
public class NoOpCache extends AbstractCachingSource {

  public NoOpCache(CommunicationSource source) {
    super(source);
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#exists(java.lang.String)
   */
  @Override
  public boolean exists(String id) {
    return source.exists(id);
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(java.lang.String)
   */
  @Override
  public Optional<Communication> get(String id) {
    return source.get(id);
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.cache.CachingSource#update(edu.jhu.hlt.concrete.Communication)
   */
  public void update(Communication c) {}

}
