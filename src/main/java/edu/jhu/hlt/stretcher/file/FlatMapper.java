package edu.jhu.hlt.stretcher.file;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FlatMapper implements FilenameMapper {

  private final Path directory;
  private final String extension;

  public FlatMapper(Path directory, String extension) {
    this.directory = directory;
    // TODO do something smarter here in case user passes ".concrete"
    this.extension = "." + extension;
  }

  @Override
  public Path map(String id) {
    return Paths.get(directory.toString(), id + extension);
  }

  @Override
  public boolean match(Path path) {
    return path.toString().endsWith(this.extension);
  }

}
