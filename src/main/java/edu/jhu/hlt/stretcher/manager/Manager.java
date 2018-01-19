/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.manager;

import com.typesafe.config.Config;

import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.stretcher.FetchImpl;
import edu.jhu.hlt.stretcher.StoreImpl;
import edu.jhu.hlt.stretcher.source.Source;
import edu.jhu.hlt.stretcher.store.Store;

/**
 * Manage fetch and store requests.
 *
 * The manager wraps a communication source and a communication store.
 * The constructor should not take any arguments.
 * Instead, the initialize() method is used to initialize the object.
 * The Config object is loaded from a configuration file.
 * @see DependencyLoader
 */
public interface Manager extends Source, Store, AutoCloseable {

  public void initialize(Source source, Store store, Config config);

  default StoreCommunicationService.Iface getStoreImpl() {
    return new StoreImpl(this);
  }

  default FetchCommunicationService.Iface getFetchImpl() {
    return new FetchImpl(this);
  }
}