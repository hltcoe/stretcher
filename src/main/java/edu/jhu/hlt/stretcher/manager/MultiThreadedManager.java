/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.manager;

import java.util.List;
import java.util.Optional;

import com.typesafe.config.Config;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.source.Source;
import edu.jhu.hlt.stretcher.store.Store;

/**
 * This manager assumes the Source and Store are thread-safe.
 * It also assumes that those components handle a simultaneous get() and save() call on the same communication.
 * The above should be very rare due to application read-write patterns and/or caching.
 */
public class MultiThreadedManager implements Manager {

  private Source src;
  private Store store;

  @Override
  public void initialize(Source source, Store store, Config config) {
    this.src = source;
    this.store = store;
  }

  @Override
  public boolean exists(String id) {
    return src.exists(id);
  }

  @Override
  public int size() {
    return src.size();
  }

  @Override
  public Optional<Communication> get(String id) {
    return src.get(id);
  }

  @Override
  public List<Communication> get(List<String> ids) {
    return src.get(ids);
  }

  @Override
  public List<Communication> get(long offset, long nToGet) {
    return src.get(offset, nToGet);
  }

  @Override
  public void save(Communication c) {
    store.save(c);
  }

  @Override
  public void close() throws Exception {
    src.close();
    store.close();
  }

}
