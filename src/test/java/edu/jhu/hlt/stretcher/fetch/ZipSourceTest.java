/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.fetch;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import edu.jhu.hlt.stretcher.fetch.ZipSource;

public class ZipSourceTest {

  public static Path getPath() {
    ClassLoader classLoader = ZipSourceTest.class.getClassLoader();
    File file = new File(classLoader.getResource("data/comms.zip").getFile());
    return Paths.get(file.getAbsolutePath());
  }

  @Test
  public void testSize() throws Exception {
    ZipSource zs = new ZipSource(getPath());
    assertEquals(4, zs.size());
    zs.close();
  }

  @Test
  public void testExists() throws Exception {
    ZipSource zs = new ZipSource(getPath());
    assertTrue(zs.exists("1"));
    assertFalse(zs.exists("NO_EXIST"));
    zs.close();
  }

  @Test
  public void testGetIteration() throws Exception {
    ZipSource zs = new ZipSource(getPath());
    // offset is greater than archive size
    assertEquals(0, zs.get(100, 10).size());
    // offset plus batch greater than archive size
    assertEquals(2, zs.get(2, 10).size());
    // Get the data in two calls
    List<String> ids = Lists.transform(zs.get(0, 2), (el) -> el.getId());
    assertTrue(ids.contains("1"));
    assertTrue(ids.contains("2"));
    ids = Lists.transform(zs.get(2, 2), (el) -> el.getId());
    assertTrue(ids.contains("3"));
    assertTrue(ids.contains("4"));
    zs.close();
  }
}
