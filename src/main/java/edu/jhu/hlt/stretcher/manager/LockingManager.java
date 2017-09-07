package edu.jhu.hlt.stretcher.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.source.CommunicationSource;
import edu.jhu.hlt.stretcher.storage.Persister;

/*
 * of course there are thread safe maps etc.
 *
 * this is a demo.
 */
public class LockingManager implements Manager {
  private final Map<UUID, Communication> comms;
  private final Lock l;
  private final Persister storage;

  // maybe want to pass in multiple sources,
  // file vs. database for example
  // this impl just bootstraps a few comms
  public LockingManager(CommunicationSource source, Persister storage) {
    this.comms = new HashMap<>();
    this.l = new ReentrantLock();
    this.storage = storage;

    for (Communication c : source.get(0, 10)) {
      this.comms.put(UUID.fromString(c.getUuid().getUuidString()), c);
    }
  }

  public LockingManager(Persister storage) {
    this.comms = new HashMap<>();
    this.l = new ReentrantLock();
    this.storage = storage;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.manager.Manager#update(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void update(Communication updated) {
    UUID uuid = UUID.fromString(updated.getUuid().getUuidString());
    l.lock();
    try {
      // add/clobber
      // smarter impls should check the source, etc.
      this.comms.put(uuid, updated);
    } finally {
      l.unlock();
    }
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.manager.Manager#persist()
   */
  @Override
  public void persist() {
    // this impl sends all its comms (!) to be stored
    for (Communication c : this.comms.values()) {
      this.storage.store(c);
    }
  }
}
