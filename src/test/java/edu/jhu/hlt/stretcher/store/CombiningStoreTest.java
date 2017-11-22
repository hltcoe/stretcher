/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.store;

import static org.junit.Assert.*;

import org.junit.Test;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.CommunicationUtility;
import edu.jhu.hlt.stretcher.combiner.Combiner;

public class CombiningStoreTest {

  @Test
  public void test() throws Exception {
    MemoryStore memStore = new MemoryStore();
    Combiner combiner = new TextAppender();
    combiner.initialize(ConfigFactory.empty());
    Store store = new CombiningStore(memStore, combiner);
    Communication c1 = CommunicationUtility.create("1", "hello");
    Communication c2 = c1.deepCopy();
    c2.setText("world");

    store.save(c1);
    assertEquals("hello", memStore.retrieve("1").getText());
    store.save(c2);
    assertEquals("hello world", memStore.retrieve("1").getText());
    store.close();
  }

  private class TextAppender implements Combiner {
    @Override
    public void initialize(Config config) {}

    @Override
    public Communication combine(Communication c1, Communication c2) {
      c1.setText(c1.getText() + " " + c2.getText());
      return c1;
    }
  }
}
