package edu.jhu.hlt.stretcher.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication;

/**
 * creates a new entry for each item.
 *
 * needless to say, you don't want to use this for anything serious,
 * but does demonstrate a "thread safe" storage engine
 */
public class EagerTarGZPersister implements Persister {

  private static final Logger LOGGER = LoggerFactory.getLogger(EagerTarGZPersister.class);

  // set up a storage executor
  // needs to be single threaded so as not to annihilate the file handle
  private final ListeningExecutorService srv;
  private final TarArchiver arch;

  public EagerTarGZPersister(OutputStream os) {
    this.srv = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
    this.arch = new TarArchiver(os);
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.storage.Persister#store(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void store(Communication c) {
    // send the storage task
    this.srv.submit(() -> {
      try {
        MDC.put("id", c.getId());
        this.arch.addEntry(new ArchivableCommunication(c));
        LOGGER.info("Stored OK");
      } catch (IOException e) {
        LOGGER.warn("Failed to store comm", e);
      } finally {
        MDC.clear();
      }
    });
  }

  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {
    this.arch.close();
    this.srv.shutdown();
    LOGGER.info("shutdown started, awaiting task termination");
    this.srv.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
  }
}
