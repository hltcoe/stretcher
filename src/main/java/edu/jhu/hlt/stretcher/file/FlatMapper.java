/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
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
    if (extension == null || extension == "") {
      this.extension = "";
    } else {
      if (extension.startsWith(".")) {
        this.extension = extension;
      } else {
        this.extension = "." + extension;
      }
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
