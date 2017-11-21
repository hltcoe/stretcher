/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.cache;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.fetch.CommunicationSource;

public abstract class AbstractCachingSource implements CachingSource {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCachingSource.class);

  protected final CommunicationSource source;

  public AbstractCachingSource(CommunicationSource source) {
    this.source = source;
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

}
