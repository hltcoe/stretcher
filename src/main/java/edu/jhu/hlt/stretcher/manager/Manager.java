package edu.jhu.hlt.stretcher.manager;

import edu.jhu.hlt.concrete.Communication;

/**
 * for the storage layer (as well as fetch/storage hybrids),
 * provide a way to manage communications coming in
 */
public interface Manager {
  public void update(Communication updated);

  /**
   * tell manager to persist its state
   *
   * of course, it's up to the impl to do whatever
   */
  public void persist();
}