/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher;

import org.apache.thrift.TException;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.concrete.services.ServiceInfo;
import edu.jhu.hlt.concrete.services.ServicesException;
import edu.jhu.hlt.stretcher.manager.Manager;
import edu.jhu.hlt.stretcher.util.ServiceUtil;

public class StoreImpl implements StoreCommunicationService.Iface, AutoCloseable {

  private final Manager mgr;

  public StoreImpl(Manager mgr) {
    this.mgr = mgr;
  }

  @Override
  public ServiceInfo about() throws TException {
    return ServiceUtil.serviceInfo();
  }

  @Override
  public boolean alive() throws TException {
    return true;
  }

  @Override
  public void store(Communication c) throws ServicesException, TException {
    this.mgr.save(c);
  }

  @Override
  public void close() {
    try {
      mgr.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
