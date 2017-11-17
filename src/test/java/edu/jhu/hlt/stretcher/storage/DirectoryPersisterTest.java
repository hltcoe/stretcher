package edu.jhu.hlt.stretcher.storage;

import static org.junit.Assert.*;

import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.CommunicationUtility;

public class DirectoryPersisterTest {
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
    Persister persister = new DirectoryPersister(root);
    Communication c = CommunicationUtility.create("99", "store test");
    persister.store(c);
    //Thread.sleep(100);
    //Optional<Communication> comm = engine.get("99");
    //assertTrue(comm.isPresent());
    //assertEquals("99", comm.get().getId());
    persister.close();
  }

}
