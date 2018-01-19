/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.file;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

public class FormatDetectorTest {

  private static Path baseDirectory;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    ClassLoader classLoader = FormatDetectorTest.class.getClassLoader();
    File file = new File(classLoader.getResource("FormatDetector/format_detector_test").getFile());
    baseDirectory = Paths.get(file.getAbsoluteFile().getParentFile().getAbsolutePath());
  }

  @Test
  public void testWithNoExtension() throws IOException {
    Path dir = baseDirectory.resolve("no_extension");
    FormatDetector detector = new FormatDetector(dir);
    assertThat(detector.getHelper(), instanceOf(UncompressedConcreteFiles.class));
    assertEquals(dir.resolve("test"), detector.getMapper().map("test"));
  }

  @Test
  public void testWithCommExtension() throws IOException {
    Path dir = baseDirectory.resolve("comm_extension");
    FormatDetector detector = new FormatDetector(dir);
    assertThat(detector.getHelper(), instanceOf(UncompressedConcreteFiles.class));
    assertEquals(dir.resolve("test.comm"), detector.getMapper().map("test"));
  }

  @Test
  public void testWithGzExtension() throws IOException {
    Path dir = baseDirectory.resolve("gz_extension");
    FormatDetector detector = new FormatDetector(dir);
    assertThat(detector.getHelper(), instanceOf(GzConcreteFiles.class));
    assertEquals(dir.resolve("test.gz"), detector.getMapper().map("test"));
  }

  @Test
  public void testWithCommGzExtension() throws IOException {
    Path dir = baseDirectory.resolve("comm_gz_extension");
    FormatDetector detector = new FormatDetector(dir);
    assertThat(detector.getHelper(), instanceOf(GzConcreteFiles.class));
    assertEquals(dir.resolve("test.comm.gz"), detector.getMapper().map("test"));
  }
}
