/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.store;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.CommunicationUtility;
import edu.jhu.hlt.stretcher.source.ZipSource;

public class ZipStoreTest {
  public static TemporaryFolder folder = new TemporaryFolder();
  public static Path root;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    folder.create();
    root = folder.getRoot().toPath();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    folder.delete();
  }

  @Test
  public void testSimpleSave() throws Exception {
    Path path = root.resolve("test1.zip");
    Store store = new ZipStore(path);
    Communication c = CommunicationUtility.create("99", "store test");
    store.save(c);
    store.close();

    ZipSource source = new ZipSource(path);
    Optional<Communication> comm = source.get("99");
    assertTrue(comm.isPresent());
    assertEquals("store test", comm.get().getText());
    source.close();
  }

  @Test
  public void testOverwrite() throws Exception {
    Path path = root.resolve("test2.zip");
    Store store = new ZipStore(path);
    Communication c1 = CommunicationUtility.create("1", "first write");
    Communication c2 = CommunicationUtility.create("2", "test");
    store.save(c1);
    store.save(c2);
    Communication c1prime = CommunicationUtility.create("1", "second write");
    store.save(c1prime);
    store.close();

    ZipSource source = new ZipSource(path);
    Optional<Communication> comm = source.get("1");
    assertTrue(comm.isPresent());
    assertEquals("second write", comm.get().getText());
    source.close();
  }

  @Test
  public void testExisitingZip() throws Exception {
    Path path = root.resolve("test3.zip");
    Store store = new ZipStore(path);
    Communication c1 = CommunicationUtility.create("1", "this is a test");
    store.save(c1);
    store.close();

    store = new ZipStore(path);
    Communication c2 = CommunicationUtility.create("2", "new file");
    store.save(c2);
    store.close();

    ZipSource source = new ZipSource(path);
    Optional<Communication> comm = source.get("1");
    assertTrue(comm.isPresent());
    assertEquals("this is a test", comm.get().getText());
    comm = source.get("2");
    assertTrue(comm.isPresent());
    assertEquals("new file", comm.get().getText());
    source.close();
  }
}
