/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.manager;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.stretcher.FetchImpl;
import edu.jhu.hlt.stretcher.StoreImpl;
import edu.jhu.hlt.stretcher.source.CommunicationSource;

/**
 * for the storage layer (as well as fetch/storage hybrids),
 * provide a way to manage communications coming in
 */
public interface Manager extends CommunicationSource, AutoCloseable {
  public void update(Communication updated);

  default StoreCommunicationService.Iface getStoreImpl() {
    return new StoreImpl(this);
  }

  default FetchCommunicationService.Iface getFetchImpl() {
    return new FetchImpl(this);
  }
}