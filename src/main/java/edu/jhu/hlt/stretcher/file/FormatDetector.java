package edu.jhu.hlt.stretcher.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Guesses the format of the filename from the first file in a directory.
 *
 * Supports:
 * commID
 * commID.gz
 * commID.comm.gz
 * commID.concrete.gz
 * commID.comm
 * commID.concrete
 */
public class FormatDetector {
  private final FilenameMapper mapper;
  private final ConcreteFiles helper;
  private static List<String> EXTENSIONS = Arrays.asList(new String[] {"comm", "concrete"});

  public FormatDetector(Path directory) throws IOException {
    Optional<Path> file = Files.list(directory).findFirst();
    if (!file.isPresent()) {
      throw new IOException("Empty directory");
    }
    String filename = file.get().getFileName().toString();
    if (!filename.contains(".")) {
      // uncompressed with no extension
      mapper = new FlatMapper(directory, false);
      helper = new UncompressedConcreteFiles();
    } else {
      String[] parts = filename.split("\\.");
      if (parts[parts.length - 1].equalsIgnoreCase("gz")) {
        // compressed
        helper = new GzConcreteFiles();
        if (EXTENSIONS.contains(parts[parts.length - 2])) {
          // comm.gz
          mapper = new FlatMapper(directory, parts[parts.length - 2] + "." + parts[parts.length - 1]);
        } else {
          // gz
          mapper = new FlatMapper(directory, parts[parts.length - 1]);
        }
      } else {
        // uncompressed
        helper = new UncompressedConcreteFiles();
        if (EXTENSIONS.contains(parts[parts.length - 1])) {
          mapper = new FlatMapper(directory, parts[parts.length - 1]);
        } else {
          mapper = new FlatMapper(directory, false);
        }
      }
    }
  }

  public FilenameMapper getMapper() {
    return mapper;
  }

  public ConcreteFiles getHelper() {
    return helper;
  }
}
