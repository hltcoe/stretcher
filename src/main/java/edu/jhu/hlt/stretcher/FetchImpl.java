package edu.jhu.hlt.stretcher;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.thrift.TException;

import com.google.common.collect.ImmutableList;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import edu.jhu.hlt.concrete.access.FetchRequest;
import edu.jhu.hlt.concrete.access.FetchResult;
import edu.jhu.hlt.concrete.services.NotImplementedException;
import edu.jhu.hlt.concrete.services.ServiceInfo;
import edu.jhu.hlt.concrete.services.ServicesException;
import edu.jhu.hlt.stretcher.source.CommunicationSource;

public class FetchImpl implements FetchCommunicationService.Iface {

  private final List<CommunicationSource> commSrc;

  private int size = -1;

  // can imagine where you want >1 src
  public FetchImpl(Iterable<CommunicationSource> srces) {
    this.commSrc = ImmutableList.copyOf(srces);
    if (this.commSrc.isEmpty())
      throw new IllegalArgumentException("need >0 sources");
  }

  @Override
  public ServiceInfo about() throws TException {
    return new ServiceInfo()
        .setName("stretcher")
        .setVersion("0.0.0-pre-alpha");
  }

  @Override
  public boolean alive() throws TException {
    return true;
  }

  @Override
  public FetchResult fetch(FetchRequest arg0) throws ServicesException, TException {
    // req comm list
    FetchResult res = new FetchResult();

    // use first source only
    CommunicationSource target = this.commSrc.get(0);

    // just do comm IDs
    // these are string IDs.. ?
    for (String cid : arg0.getCommunicationIds()) {
      target.get(cid).ifPresent(res::addToCommunications);
    }

    return res;
  }

  @Override
  public long getCommunicationCount() throws NotImplementedException, TException {
    // this is tricky if there's a stream powering the source
    // wastefully/cleverly cache the amt of comms
    // or maybe just throw if the sources are unbound
    if (size == -1) {
      // iterate over all the first source's comms
      size = this.commSrc.get(0).get(0, Long.MAX_VALUE).size();
    }

    return size;
  }

  @Override
  public List<String> getCommunicationIDs(long offset, long limit) throws NotImplementedException, TException {
    return this.commSrc.get(0).get(offset, limit)
        .stream()
        .map(Communication::getId)
        .collect(Collectors.toList());
  }
}
