package edu.jhu.hlt.stretcher.fetch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;

import edu.jhu.hlt.concrete.Communication;

/**
 * Caching wrapper for a communication source.
 *
 * Use this if you expect access patterns will result in the same communications
 * requested over a short period of time.
 * If the source and persister are using the same directory or file, the update()
 * should be called to update the cache.
 */
public class CachingSource implements CommunicationSource {
  private static final Logger LOGGER = LoggerFactory.getLogger(CachingSource.class);

  private static final long DEFAULT_MAX_SIZE = 1000L;

  private final CommunicationSource source;
  private final ConcurrentMap<String, Communication> cache;

  public CachingSource(CommunicationSource source) {
    this(source, DEFAULT_MAX_SIZE);
  }

  public CachingSource(CommunicationSource source, long size) {
    this.source = source;
    this.cache = CacheBuilder.newBuilder().maximumSize(size).<String, Communication>build().asMap();
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#exists(java.lang.String)
   */
  @Override
  public boolean exists(String id) {
    return cache.containsKey(id) || source.exists(id);
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#size()
   */
  @Override
  public int size() {
    return source.size();
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(java.lang.String)
   */
  @Override
  public Optional<Communication> get(String id) {
    Communication c = cache.get(id);
    if (c != null) {
      LOGGER.debug("Cache hit for " + id);
      return Optional.of(c);
    } else {
      Optional<Communication> opt = source.get(id);
      if (opt.isPresent()) {
        cache.put(id, opt.get());
      }
      return opt;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(java.util.List)
   */
  @Override
  public List<Communication> get(List<String> ids) {
    List<Communication> comms = new ArrayList<Communication>();
    for (String id : ids) {
      this.get(id).ifPresent(comms::add);
    }
    LOGGER.info("Returning " + comms.size() + " communications");
    return comms;
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(long, long)
   */
  @Override
  public List<Communication> get(long offset, long nToGet) {
    return source.get(offset, nToGet);
  }

  public void update(Communication c) {
    cache.replace(c.getId(), c);
  }

}
