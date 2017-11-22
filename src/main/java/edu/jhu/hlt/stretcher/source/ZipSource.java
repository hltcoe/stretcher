/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.source;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

public class ZipSource implements Source, AutoCloseable {

  private ZipFile zf;
  private String extension = "comm";
  private CompactCommunicationSerializer ser = new CompactCommunicationSerializer();

  public ZipSource(Path path) throws IOException {
    zf = new ZipFile(path.toAbsolutePath().toString());
  }

  private String getFilename(String id) {
    return id + "." + extension;
  }

  @Override
  public boolean exists(String id) {
    return zf.getEntry(getFilename(id)) != null;
  }

  private Communication load(ZipArchiveEntry zae) throws ZipLoadException {
    try {
      InputStream is = zf.getInputStream(zae);
      byte[] bytes = IOUtils.toByteArray(is);
      return ser.fromBytes(bytes);
    } catch (IOException | ConcreteException ex) {
      throw new ZipLoadException(ex);
    }
  }

  private Communication load(String filename) throws ZipLoadException {
    ZipArchiveEntry zae = zf.getEntry(filename);
    if (zae == null) {
      throw new ZipLoadException("Does not exist");
    }
    return load(zae);
  }

  @Override
  public Optional<Communication> get(String id) {
    try {
      Communication comm = load(getFilename(id));
      return Optional.of(comm);
    } catch (ZipLoadException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<Communication> get(List<String> ids) {
    List<Communication> comms = new ArrayList<Communication>();
    for (String id : ids) {
      this.get(id).ifPresent(comms::add);
    }
    return comms;
  }

  @Override
  public List<Communication> get(long offset, long nToGet) {
    List<Communication> comms = new ArrayList<Communication>();
    Enumeration<ZipArchiveEntry> e = zf.getEntriesInPhysicalOrder();
    for (int i = 0; i < offset; i++) {
      if (e.hasMoreElements()) {
        e.nextElement();
      } else {
        break;
      }
    }
    for (int i = 0; i < nToGet; i++) {
      if (e.hasMoreElements()) {
        ZipArchiveEntry zae = e.nextElement();
        try {
          comms.add(load(zae));
        } catch (ZipLoadException ex) {
          // something corrupted with the zip file?
          throw new RuntimeException("Error getting file from zip file", ex);
        }
      } else {
        break;
      }
    }
    return comms;
  }

  @Override
  public int size() {
    int count = 0;
    for (Enumeration<ZipArchiveEntry> e = zf.getEntries(); e.hasMoreElements(); e.nextElement()) {
      count++;
    }
    return count;
  }

  @Override
  public void close() throws Exception {
    zf.close();
  }

  private class ZipLoadException extends Exception {
    private static final long serialVersionUID = 5147108783293171841L;

    public ZipLoadException(String message) {
      super(message);
    }

    public ZipLoadException(Throwable throwable) {
      super(throwable);
    }
  }
}
