/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.manager;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.typesafe.config.Config;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.fetch.CommunicationSource;
import edu.jhu.hlt.stretcher.store.Persister;

/**
 * Basic manager for reading and writing communications.
 *
 * Lock across fetch and store for any operation.
 */
public class LockingManager implements Manager {

  private final CommunicationSource src;
  private final Persister storage;
  private final Lock lock;

  public LockingManager(CommunicationSource source, Persister storage, Config config) {
    this.lock = new ReentrantLock();
    this.src = source;
    this.storage = storage;
  }

  @Override
  public int size() {
    lock.lock();
    int count = src.size();
    lock.unlock();
    return count;
  }

  @Override
  public boolean exists(String id) {
    lock.lock();
    boolean exists = src.exists(id);
    lock.unlock();
    return exists;
  }

  @Override
  public Optional<Communication> get(String id) {
    lock.lock();
    Optional<Communication> comm = src.get(id);
    lock.unlock();
    return comm;
  }

  @Override
  public List<Communication> get(List<String> ids) {
    lock.lock();
    List<Communication> comms = src.get(ids);
    lock.unlock();
    return comms;
  }

  @Override
  public List<Communication> get(long offset, long nToGet) {
    lock.lock();
    List<Communication> comms = src.get(offset, nToGet);
    lock.unlock();
    return comms;
  }

  @Override
  public void store(Communication updated) {
    lock.lock();
    storage.store(updated);
    lock.unlock();
  }

  @Override
  public void close() throws Exception {
    lock.lock();
    this.storage.close();
    lock.unlock();
  }

}
