/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.metadata.AnnotationMetadataFactory;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

public class CommunicationUtility {

  public static Communication create(String id, String text) {
    AnnotationMetadata amd = AnnotationMetadataFactory.fromCurrentLocalTime("unit test tool");
    Communication c = new Communication();
    c.setId(id);
    c.setUuid(UUIDFactory.newUUID());
    c.setText(text);
    c.setMetadata(amd);
    c.setType("no");
    return c;
  }

  public static void save(Path path, String id, String text) throws ConcreteException, IOException {
    Communication c = create(id, text);
    CommunicationSerializer serializer = new CompactCommunicationSerializer();
    byte[] data = serializer.toBytes(c);
    Files.write(path, data, StandardOpenOption.CREATE);
  }

}
