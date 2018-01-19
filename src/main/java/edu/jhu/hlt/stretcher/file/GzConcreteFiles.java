/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.file;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

public class GzConcreteFiles implements ConcreteFiles {
  private final CommunicationSerializer serializer = new CompactCommunicationSerializer();

  @Override
  public Communication read(Path path) throws ConcreteException {
    try (InputStream is = Files.newInputStream(path);
         GZIPInputStream gzis = new GZIPInputStream(is);
         BufferedInputStream bis = new BufferedInputStream(gzis)) {
       byte[] bytes = IOUtils.toByteArray(bis);
       return serializer.fromBytes(new Communication(), bytes);
     } catch (IOException e) {
         throw new ConcreteException(e);
     }
  }

  @Override
  public void write(Path path, Communication c) throws ConcreteException {
    try (OutputStream os = Files.newOutputStream(path);
         GZIPOutputStream gzos = new GZIPOutputStream(os);) {
      byte[] bytes = serializer.toBytes(c);
      gzos.write(bytes);
    } catch (IOException e) {
      throw new ConcreteException(e);
    }
  }

}
