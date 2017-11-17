/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.filter;

import edu.jhu.hlt.concrete.Communication;

public class NoOpFilter implements CommunicationFilter {

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.filter.CommunicationFilter#filter(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void filter(Communication c) {}

}
