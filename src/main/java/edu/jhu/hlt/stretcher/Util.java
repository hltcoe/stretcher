package edu.jhu.hlt.stretcher;

import edu.jhu.hlt.concrete.services.ServiceInfo;

class Util {
  public static ServiceInfo serviceInfo() {
    return new ServiceInfo()
        .setName("stretcher")
        .setVersion("0.0.1");
  }
}
