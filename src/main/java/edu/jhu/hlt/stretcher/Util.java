/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher;

import edu.jhu.hlt.concrete.services.ServiceInfo;

class Util {
  public static ServiceInfo serviceInfo() {
    return new ServiceInfo()
        .setName("stretcher")
        .setVersion("0.0.1");
  }
}
