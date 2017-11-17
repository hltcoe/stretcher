package edu.jhu.hlt.stretcher.source;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.CommunicationUtility;

public class CachingSourceTest {

  private Map<String, Communication> map =
          Collections.synchronizedMap(new HashMap<String, Communication>());

  @Before
  public void initialize() {
    map.clear();
    map.put("1", CommunicationUtility.create("1", "test 1"));
    map.put("2", CommunicationUtility.create("2", "test 2"));
    map.put("3", CommunicationUtility.create("3", "test 3"));
    map.put("4", CommunicationUtility.create("4", "test 4"));
    map.put("5", CommunicationUtility.create("5", "test 5"));
  }

  @Test
  public void testExists() {
    CommunicationSource source = new CachingSource(new MemorySource(map));
    assertTrue(source.exists("2"));
    assertFalse(source.exists("0"));
  }

  @Test
  public void testSize() {
    CommunicationSource source = new CachingSource(new MemorySource(map));
    assertEquals(map.size(), source.size());
  }

  @Test
  public void testGet() {
    CommunicationSource source = new CachingSource(new MemorySource(map));
    assertEquals(map.get("3"), source.get("3").get());
    assertFalse(source.get("0").isPresent());
  }

  @Test
  public void testUpdate() {
    CachingSource source = new CachingSource(new MemorySource(map));
    assertEquals(map.get("4"), source.get("4").get());
    source.update(CommunicationUtility.create("4", "New"));
    assertEquals("New", source.get("4").get().getText());
  }
}
