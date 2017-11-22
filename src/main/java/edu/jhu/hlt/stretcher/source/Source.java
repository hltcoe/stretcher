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
 * A source of communications for a fetch service.
 */
public interface Source extends AutoCloseable {

  /**
   * Does this communication exist in the source
   * @param id Communication ID
   * @return boolean
   */
  public boolean exists(String id);

  /**
   * Get the specified communication
   * @param id Communication ID
   * @return an {@link Optional} wrapping a comm or empty if this comm isn't here
   */
  public Optional<Communication> get(String id);

  /**
   * Get a list of communications based on IDs
   * @param uuids
   * @return List of communications
   */
  public List<Communication> get(List<String> ids);

  /**
   * Supports iteration over all communications
   * @param offset
   * @param nToGet
   * @return List of communications
   */
  public List<Communication> get(long offset, long nToGet);

  /**
   * Get the number of communications in the source
   * @return
   */
  public int size();
}