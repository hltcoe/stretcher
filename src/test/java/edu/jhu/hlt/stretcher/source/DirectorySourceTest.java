/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.source;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.CommunicationUtility;
import edu.jhu.hlt.stretcher.file.FormatDetector;

public class DirectorySourceTest {
  public static TemporaryFolder folder = new TemporaryFolder();
  public static Path root;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    folder.create();
    root = folder.getRoot().toPath();
    Path filePath = Paths.get(root.toString(), "1.comm");
    CommunicationUtility.save(filePath, "1", "this is a test");
    filePath = Paths.get(root.toString(), "2.comm");
    CommunicationUtility.save(filePath, "2", "this is a test2");
    filePath = Paths.get(root.toString(), "3.comm");
    CommunicationUtility.save(filePath, "3", "this is a test3");
    filePath = Paths.get(root.toString(), "4.comm");
    CommunicationUtility.save(filePath, "4", "this is a test4");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    folder.delete();
  }

  private static DirectorySource getSource() throws IOException {
    FormatDetector detector = new FormatDetector(root);
    return new DirectorySource(root, detector.getMapper(), detector.getHelper());
  }

  @Test
  public void testExists() throws Exception {
    Source source = getSource();
    assertTrue(source.exists("1"));
    assertFalse(source.exists("0"));
  }

  @Test
  public void testSize() throws Exception {
    Source source = getSource();
    assertEquals(4, source.size());
  }

  @Test
  public void testGetById() throws Exception {
    Source source = getSource();
    Optional<Communication> comm = source.get("1");
    assertTrue(comm.isPresent());
    assertEquals("1", comm.get().getId());
    assertFalse(source.get("76").isPresent());
  }

  @Test
  public void testGetByList() throws Exception {
    Source source = getSource();
    List<Communication> list = source.get(Arrays.asList("0", "1", "2"));
    assertEquals(2, list.size());
  }

  @Test
  public void testGetIterator() throws Exception {
    Source source = getSource();
    List<Communication> list = source.get(0, 2);
    assertEquals(2, list.size());
    assertEquals("1", list.get(0).getId());
    assertEquals("2", list.get(1).getId());
    list = source.get(2, 2);
    assertEquals(2, list.size());
    assertEquals("3", list.get(0).getId());
    assertEquals("4", list.get(1).getId());
    list = source.get(4, 2);
    assertEquals(0, list.size());
  }

}
