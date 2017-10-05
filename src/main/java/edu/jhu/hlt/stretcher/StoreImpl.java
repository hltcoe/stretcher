package edu.jhu.hlt.stretcher;



import org.apache.thrift.TException;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.concrete.services.ServiceInfo;
import edu.jhu.hlt.concrete.services.ServicesException;
import edu.jhu.hlt.stretcher.manager.Manager;

public class StoreImpl implements StoreCommunicationService.Iface {

  private final Manager mgr;

  public StoreImpl(Manager mgr) {
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
  public void store(Communication arg0) throws ServicesException, TException {
    // pass the buck
    this.mgr.update(arg0);
  }
}
