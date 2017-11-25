/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.file;

import static org.junit.Assert.*;

import java.nio.file.Paths;

import org.junit.Test;

public class FlatMapperTest {

  @Test
  public void testWithNoDir() {
    FlatMapper mapper = new FlatMapper(Paths.get(""), "comm");
    assertEquals("1.comm", mapper.map("1").toString());
  }

}
