package edu.jhu.hlt.stretcher.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.MoreExecutors;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.stretcher.source.CommunicationSource;
import edu.jhu.hlt.stretcher.storage.Persister;

/**
 * Fetch and store communications from a directory.
 * Store overwrites the original communication files.
 */
public class SimpleFileEngine implements CommunicationSource, Persister {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFileEngine.class);

  private final Path directory;
  private final CommunicationSerializer serializer;
  private final ExecutorService executorService;

  public SimpleFileEngine(Path directory) throws IOException {
    this.serializer = new CompactCommunicationSerializer();
    this.executorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
    this.directory = directory.toAbsolutePath();
    validateDirectory(this.directory);
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#exists(java.lang.String)
   */
  @Override
  public boolean exists(String id) {
    return Files.exists(getPath(id));
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#size()
   */
  @Override
  public int size() {
    AtomicInteger counter = new AtomicInteger(0);
    try {
      Files.newDirectoryStream(directory,
              path -> path.toString().endsWith(".comm"))
              .forEach(path -> counter.getAndIncrement());
    } catch (IOException e) {
      LOGGER.error("Failed to count files in " + directory.toString(), e);
    }
    return counter.get();
  }

  /*
   * (non-Javadoc)
   *
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(java.lang.String)
   */
  @Override
  public Optional<Communication> get(String id) {
    return load(getPath(id));
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
    return comms;
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.source.CommunicationSource#get(long, long)
   */
  @Override
  public List<Communication> get(long offset, long nToGet) {
    try {
      return Files.list(directory)
              .sorted()
              .skip(offset)
              .limit(nToGet)
              .map(this::load)
              .filter(c -> c.isPresent())
              .map(c -> c.get())
              .collect(Collectors.toList());
    } catch (IOException e) {
      LOGGER.warn("Unable to open " + directory.toString());
      return Collections.emptyList();
    }
  }

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.storage.Persister#store(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void store(Communication c) {
    this.executorService.submit(() -> {
      try {
        byte[] data = serializer.toBytes(c);
        Files.write(this.getPath(c.getId()), data);
      } catch (ConcreteException | IOException e) {
        LOGGER.warn("Unable to store " + c.getId(), e);
      }
    });
  }

  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {
    this.executorService.shutdown();
    LOGGER.info("Shutdown triggered; awaiting task termination");
    this.executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
  }

  private static void validateDirectory(Path directory) throws IOException {
    if (!Files.exists(directory)) {
      throw new IOException(directory.toString() + " does not exist");
    }
    try {
      Files.list(directory);
    } catch (IOException e) {
      throw new IOException("Cannot open " + directory.toString(), e);
    }
  }

  private Path getPath(String id) {
    return Paths.get(directory.toString(), id + ".comm");
  }

  private Optional<Communication> load(Path path) {
    try {
      Communication comm = serializer.fromPath(path);
      return Optional.of(comm);
    } catch (ConcreteException e) {
      return Optional.empty();
    }
  }

}
