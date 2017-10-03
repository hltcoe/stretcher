package edu.jhu.hlt.stretcher.file;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.CommunicationUtility;

public class SimpleFileEngineTest {

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

  @Test
  public void testExists() throws Exception {
    SimpleFileEngine engine = new SimpleFileEngine(root);
    assertTrue(engine.exists("1"));
    assertFalse(engine.exists("0"));
    engine.close();
  }

  @Test
  public void testGetById() throws Exception {
    SimpleFileEngine engine = new SimpleFileEngine(root);
    Optional<Communication> comm = engine.get("1");
    assertTrue(comm.isPresent());
    assertEquals("1", comm.get().getId());
    assertFalse(engine.get("76").isPresent());
    engine.close();
  }

  @Test
  public void testGetByList() throws Exception {
    SimpleFileEngine engine = new SimpleFileEngine(root);
    List<Communication> list = engine.get(Arrays.asList("0", "1", "2"));
    assertEquals(2, list.size());
    engine.close();
  }

  @Test
  public void testGetIterator() throws Exception {
    SimpleFileEngine engine = new SimpleFileEngine(root);
    List<Communication> list = engine.get(0, 2);
    assertEquals(2, list.size());
    assertEquals("1", list.get(0).getId());
    assertEquals("2", list.get(1).getId());
    list = engine.get(2, 2);
    assertEquals(2, list.size());
    assertEquals("3", list.get(0).getId());
    assertEquals("4", list.get(1).getId());
    list = engine.get(4, 2);
    assertEquals(0, list.size());
    engine.close();
  }

  @Ignore
  public void testStore() throws Exception {
    SimpleFileEngine engine = new SimpleFileEngine(root);
    Communication c = CommunicationUtility.create("99", "store test");
    engine.store(c);
    Thread.sleep(100);
    Optional<Communication> comm = engine.get("99");
    assertTrue(comm.isPresent());
    assertEquals("99", comm.get().getId());
    engine.close();
  }

}
