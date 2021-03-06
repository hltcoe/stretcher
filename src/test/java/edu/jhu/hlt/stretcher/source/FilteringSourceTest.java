/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.source;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.CommunicationUtility;
import edu.jhu.hlt.stretcher.filter.Filter;

public class FilteringSourceTest {

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
  public void test() throws Exception {
    Filter filter = new DeleteFilter();
    filter.initialize(ConfigFactory.empty());
    Source source = new FilteringSource(new MemorySource(map), filter);
    assertEquals("", source.get("1").get().getText());
    source.close();
  }

  private class DeleteFilter implements Filter {
    public void initialize(Config config) {}
    public void filter(Communication c) {
      c.setText("");
    }
  }
}
