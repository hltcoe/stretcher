/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.store;

import static org.junit.Assert.*;

import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.CommunicationUtility;
import edu.jhu.hlt.stretcher.file.FlatMapper;
import edu.jhu.hlt.stretcher.file.UncompressedConcreteFiles;

public class DirectoryStoreTest {
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

  @Ignore
  public void testStore() throws Exception {
    Store store = new DirectoryStore(new FlatMapper(root, "comm"), new UncompressedConcreteFiles());
    Communication c = CommunicationUtility.create("99", "store test");
    store.save(c);
    //Thread.sleep(100);
    //Optional<Communication> comm = engine.get("99");
    //assertTrue(comm.isPresent());
    //assertEquals("99", comm.get().getId());
    store.close();
  }

}
