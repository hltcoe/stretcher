/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.thrift.TException;

import com.typesafe.config.Config;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import edu.jhu.hlt.concrete.access.FetchRequest;
import edu.jhu.hlt.concrete.access.FetchResult;
import edu.jhu.hlt.concrete.services.NotImplementedException;
import edu.jhu.hlt.concrete.services.ServiceInfo;
import edu.jhu.hlt.concrete.services.ServicesException;
import edu.jhu.hlt.stretcher.manager.Manager;
import edu.jhu.hlt.stretcher.util.ServiceUtil;

public class FetchImpl implements FetchCommunicationService.Iface, AutoCloseable {

  private static final String CONFIG_MAX = "stretcher.fetch.max";
  private final Manager mgr;
  private final long maxNumComms;

  public FetchImpl(Manager mgr, Config config) {
    this.mgr = mgr;
    this.maxNumComms = config.getLong(CONFIG_MAX);
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
  public FetchResult fetch(FetchRequest request) throws ServicesException, TException {
    if (maxNumComms > 0 && request.getCommunicationIds().size() > maxNumComms){
      throw new ServicesException("Requested more than " + maxNumComms + " communications");
    }
    FetchResult res = new FetchResult();
    res.setCommunications(new ArrayList<Communication>());
    this.mgr.get(request.getCommunicationIds()).forEach(res::addToCommunications);
    return res;
  }

  @Override
  public long getCommunicationCount() throws NotImplementedException, TException {
    return this.mgr.size();
  }

  @Override
  public List<String> getCommunicationIDs(long offset, long limit) throws NotImplementedException, TException {
    return this.mgr.get(offset, limit)
        .stream()
        .map(Communication::getId)
        .collect(Collectors.toList());
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
