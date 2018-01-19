/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

public class UncompressedConcreteFiles implements ConcreteFiles {
  private final CommunicationSerializer serializer = new CompactCommunicationSerializer();

  @Override
  public Communication read(Path path) throws ConcreteException {
    return serializer.fromPath(path);
  }

  @Override
  public void write(Path path, Communication c) throws ConcreteException {
    byte[] data = serializer.toBytes(c);
    try {
      Files.write(path, data);
    } catch (IOException e) {
      throw new ConcreteException(e);
    }
  }

}
