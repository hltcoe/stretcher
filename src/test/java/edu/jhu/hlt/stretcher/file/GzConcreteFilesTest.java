/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.file;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.stretcher.CommunicationUtility;

public class GzConcreteFilesTest {

  public static TemporaryFolder folder = new TemporaryFolder();
  public static Path root;

  @Test
  public void test() throws IOException, ConcreteException {
    folder.create();
    root = folder.getRoot().toPath();
    Path file = root.resolve("test.comm.gz");

    ConcreteFiles files = new GzConcreteFiles();
    Communication c1 = CommunicationUtility.create("test", "this is a test");
    files.write(file, c1);
    Communication c2 = files.read(file);

    assertEquals(c1.getId(), c2.getId());
    assertEquals(c1.getUuid(), c2.getUuid());
    assertEquals(c1.getText(), c2.getText());

    folder.delete();
  }

}
