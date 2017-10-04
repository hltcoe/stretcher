package edu.jhu.hlt.stretcher.file;

import java.nio.file.Path;

public interface FilenameMapper {
  /**
   * Map a communication id to a file path
   * @param id The ID of the concrete communication.
   * @return Path of the file.
   */
  public Path map(String id);

  /**
   * Does this path match the format for a communication?
   * @param path The Path to match.
   * @return whether this is a match
   */
  public boolean match(Path path);
}
