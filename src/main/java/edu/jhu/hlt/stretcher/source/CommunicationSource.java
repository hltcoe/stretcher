/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.source;

import java.util.List;
import java.util.Optional;

import edu.jhu.hlt.concrete.Communication;

/**
 * responsible for serving up comms to the manager
 *
 * may need to get them from a DB, files, etc.
 *
 * the intent is support fetch only. so no saving in this interface.
 * see manager package.
 */
public interface CommunicationSource {

  public boolean exists(String id);

  /**
   *
   * @param id
   * @return an {@link Optional} wrapping a comm or empty if this comm isn't here
   */
  public Optional<Communication> get(String id);

  /**
   *
   * @param offset
   * @param nToGet
   * @return
   */
  public List<Communication> get(long offset, long nToGet);

  /**
   *
   * @param uuids
   * @return
   */
  public List<Communication> get(List<String> ids);

  /**
   *
   * @return
   */
  public int size();
}