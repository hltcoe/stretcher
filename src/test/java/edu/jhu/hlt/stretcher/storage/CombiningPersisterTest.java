/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.storage;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.CommunicationUtility;
import edu.jhu.hlt.stretcher.combiner.CommunicationCombiner;

public class CombiningPersisterTest {

  @Test
  public void test() {
    MemoryPersister store = new MemoryPersister();
    Persister persister = new CombiningPersister(store, new TextAppender());
    Communication c1 = CommunicationUtility.create("1", "hello");
    Communication c2 = c1.deepCopy();
    c2.setText("world");

    persister.store(c1);
    assertEquals("hello", store.retrieve("1").getText());
    persister.store(c2);
    assertEquals("hello world", store.retrieve("1").getText());
  }

  private class TextAppender implements CommunicationCombiner {
    @Override
    public Communication combine(Communication c1, Communication c2) {
      c1.setText(c1.getText() + " " + c2.getText());
      return c1;
    }
  }
}
