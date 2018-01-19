/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtility {

  /**
   * Validate that this directory exists and the application can list it.
   *
   * @param directory The path of the directory
   * @throws IOException
   */
  public static void validateDirectory(Path directory) throws IOException {
    if (!Files.exists(directory)) {
      throw new IOException(directory.toString() + " does not exist");
    }
    try {
      Files.list(directory);
    } catch (IOException e) {
      throw new IOException("Cannot open " + directory.toString(), e);
    }
  }
}
