/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.file;

import java.nio.file.Path;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

public interface ConcreteFiles {
  public Communication read(Path path) throws ConcreteException;
  public void write(Path path, Communication c) throws ConcreteException;
}
