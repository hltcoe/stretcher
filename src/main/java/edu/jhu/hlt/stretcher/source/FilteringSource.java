/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
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
import edu.jhu.hlt.stretcher.filter.Filter;

/**
 * Filter communications before sending a response.
 *
 * Use before CachingSource in a cascade.
 */
public class FilteringSource implements Source {
  private static final Logger LOGGER = LoggerFactory.getLogger(FilteringSource.class);

  private final Source source;
  private final Filter filter;

  public FilteringSource(Source source, Filter filter) {
    this.source = source;
    this.filter = filter;
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.Source#exists(java.lang.String)
   */
  @Override
  public boolean exists(String id) {
    return source.exists(id);
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
    Optional<Communication> optional = source.get(id);
    if (optional.isPresent()) {
      filter.filter(optional.get());
    }
    return optional;
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

  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {
    source.close();
  }

}
