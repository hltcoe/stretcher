/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
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

  public FlatMapper(Path directory, boolean hasExtension) {
    this.directory = directory;
    if (!hasExtension) {
      this.extension = "";
    } else {
      throw new RuntimeException("Only call this constructor with hasExtension as false");
    }
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
