package edu.jhu.hlt.stretcher.manager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;

import edu.jhu.hlt.concrete.Communication;

class CacheUpdater implements FutureCallback<Optional<Communication>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CacheUpdater.class);

  private final Lock lockPtr;
  private final Map<String, Communication> cache;

  public CacheUpdater(Lock lockPtr, Map<String, Communication> cache) {
    this.lockPtr = lockPtr;
    this.cache = cache;
  }

  @Override
  public void onFailure(Throwable arg0) {
    LOGGER.warn("Failed to update internal cache", arg0);
  }

  @Override
  public void onSuccess(Optional<Communication> arg0) {
    arg0.ifPresent(this::updateCache);
  }

  void updateCache(Communication... comms) {
    this.lockPtr.lock();
    try {
      for (Communication c : comms)
        this.cache.put(c.getId(), c);
    } finally {
      this.lockPtr.unlock();
    }
  }
}
