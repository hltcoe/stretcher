/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.filter;

import com.typesafe.config.Config;

import edu.jhu.hlt.concrete.Communication;

public class NoOpFilter implements Filter {

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.filter.Filter#initialize(com.typesafe.config.Config)
   */
  @Override
  public void initialize(Config config) {}

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.filter.Filter#filter(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void filter(Communication c) {}

}
