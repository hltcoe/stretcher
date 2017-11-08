/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.source.CommunicationSource;
import edu.jhu.hlt.stretcher.storage.Persister;

/*
 * of course there are thread safe maps etc.
 *
 * this is a demo.
 */
public class LockingManager implements Manager {

  private final NavigableMap<String, Communication> comms;
  private final Lock l;

  private final CommunicationSource src;
  private final Persister storage;

  private final ListeningExecutorService les;
  private final CacheUpdater cu;

  // maybe want to pass in multiple sources,
  // file vs. database for example
  // this impl just bootstraps a few comms
  public LockingManager(CommunicationSource source, Persister storage) {
    this.comms = new TreeMap<>();
    this.l = new ReentrantLock();

    this.src = source;
    this.storage = storage;
    this.les = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    this.cu = new CacheUpdater(this.l, this.comms);

    for (Communication c : source.get(0, 10)) {
      this.comms.put(c.getId(), c);
    }
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.manager.Manager#update(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void update(Communication updated) {
    l.lock();
    try {
      // defensive copy that is not public
      Communication cpy = new Communication(updated);
      // add/clobber
      // smarter impls should check the source, etc.
      this.comms.put(cpy.getId(), cpy);
      this.persist(cpy);
    } finally {
      l.unlock();
    }
  }

  public void persist(Communication c) {
    this.storage.store(c);
  }

  @Override
  public boolean exists(String id) {
    boolean inMap = this.comms.containsKey(id);
    if (inMap)
      return true;
    else {
      // look for it in the source
      boolean viaSrc = this.src.exists(id);
      if (viaSrc) {
        // update internal map async
        ListenableFuture<Optional<Communication>> future = this.les.submit(() -> this.src.get(id));
        Futures.addCallback(future, this.cu, this.les);
      }

      return viaSrc;
    }
  }

  @Override
  public Optional<Communication> get(String id) {
    if (this.comms.containsKey(id))
      return Optional.of(this.comms.get(id));

    return this.src.get(id);
  }

  @Override
  public List<Communication> get(long offset, long nToGet) {
    return this.src.get(offset, nToGet);
  }

  @Override
  public List<Communication> get(List<String> ids) {
    ImmutableList.Builder<Communication> bldr = ImmutableList.builder();
    List<String> cpy = new ArrayList<>(ids);
    Iterator<String> cpyIter = cpy.iterator();
    while (cpyIter.hasNext()) {
      String next = cpyIter.next();
      if (this.comms.containsKey(next)) {
        bldr.add(this.comms.get(next));
        // remove from backing list - no need to hit src for this
        cpyIter.remove();
      }
    }

    // if items remain that were not cached, get them
    if (!cpy.isEmpty()) {
      List<Communication> viaSrc = this.src.get(cpy);
      Communication[] array = viaSrc.toArray(new Communication[viaSrc.size()]);
      this.les.submit(() -> this.cu.updateCache(array));
      bldr.addAll(viaSrc);
    }

    return bldr.build();
  }

  @Override
  public int size() {
    return this.comms.size();
  }

  @Override
  public void close() throws Exception {
    this.les.shutdown();
    this.storage.close();
    this.les.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
  }
}
