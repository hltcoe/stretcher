package edu.jhu.hlt.stretcher;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.thrift.TException;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import edu.jhu.hlt.concrete.access.FetchRequest;
import edu.jhu.hlt.concrete.access.FetchResult;
import edu.jhu.hlt.concrete.services.NotImplementedException;
import edu.jhu.hlt.concrete.services.ServiceInfo;
import edu.jhu.hlt.concrete.services.ServicesException;
import edu.jhu.hlt.stretcher.manager.Manager;

public class FetchImpl implements FetchCommunicationService.Iface {

  private final Manager mgr;

  // can imagine where you want >1 src
  public FetchImpl(Manager mgr) {
    this.mgr = mgr;
  }

  @Override
  public ServiceInfo about() throws TException {
    return Util.serviceInfo();
  }

  @Override
  public boolean alive() throws TException {
    return true;
  }

  @Override
  public FetchResult fetch(FetchRequest arg0) throws ServicesException, TException {
    // req comm list
    FetchResult res = new FetchResult();

    // just do comm IDs
    this.mgr.get(arg0.getCommunicationIds()).forEach(res::addToCommunications);
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
}
