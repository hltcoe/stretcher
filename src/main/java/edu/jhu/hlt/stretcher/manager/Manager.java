/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.manager;

import com.typesafe.config.Config;

import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.stretcher.FetchImpl;
import edu.jhu.hlt.stretcher.StoreImpl;
import edu.jhu.hlt.stretcher.fetch.CommunicationSource;
import edu.jhu.hlt.stretcher.store.Persister;

/**
 * Manage fetch and store requests.
 *
 * The manager wraps a communication source and a store persister.
 * The constructor should not take any arguments.
 * Instead, the initialize() method is used to initialize the object.
 * The Config object is loaded from a configuration file.
 * @see DependencyLoader
 */
public interface Manager extends CommunicationSource, Persister, AutoCloseable {

  public void initialize(CommunicationSource source, Persister persister, Config config);

  default StoreCommunicationService.Iface getStoreImpl() {
    return new StoreImpl(this);
  }

  default FetchCommunicationService.Iface getFetchImpl() {
    return new FetchImpl(this);
  }
}