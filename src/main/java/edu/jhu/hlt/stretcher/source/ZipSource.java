/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.source;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.stretcher.file.FilenameMapper;
import edu.jhu.hlt.stretcher.file.FlatMapper;

/**
 * Load communications from a zip archive.
 *
 * Modifications to the archive while running may not affect the returned communications.
 */
public class ZipSource implements Source {
  private static final Logger LOGGER = LoggerFactory.getLogger(ZipSource.class);

  private final ZipFile zf;
  private final CompactCommunicationSerializer ser = new CompactCommunicationSerializer();
  private final FilenameMapper mapper;

  public ZipSource(Path path) throws IOException {
    zf = new ZipFile(path.toAbsolutePath().toString());
    String dir = getPrimaryDir();
    String extension = new ExtensionFinder().getExtension(zf);
    if (extension == null) {
      LOGGER.warn("Did not find any communications in " + path.toString());
      extension = "comm";
    }
    mapper = new FlatMapper(Paths.get(dir), extension);
  }

  private String getFilename(String id) {
    return mapper.map(id).toString();
  }

  @Override
  public boolean exists(String id) {
    return zf.getEntry(getFilename(id)) != null;
  }

  @Override
  public int size() {
    int count = 0;
    for (Enumeration<ZipArchiveEntry> e = zf.getEntries(); e.hasMoreElements();) {
      ZipArchiveEntry zae = e.nextElement();
      if (!zae.isDirectory()) {
        count++;
      }
    }
    return count;
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

  // take a guess that first entry is a directory if it exists
  private String getPrimaryDir() {
    String dir = "";
    Enumeration<ZipArchiveEntry> e = zf.getEntriesInPhysicalOrder();
    if (e.hasMoreElements()) {
      ZipArchiveEntry zae = e.nextElement();
      if (zae.isDirectory()) {
        dir = zae.getName();
      }
    }
    return dir;
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
        ZipArchiveEntry zae = e.nextElement();
        // ignore directories by rewinding the counter one
        if (zae.isDirectory()) {
          i--;
        }
      } else {
        break;
      }
    }
    for (int i = 0; i < nToGet; i++) {
      if (e.hasMoreElements()) {
        ZipArchiveEntry zae = e.nextElement();
        try {
          if (zae.isDirectory()) {
            // ignore directories by rewinding the counter one
            if (zae.isDirectory()) {
              i--;
            }
          } else {
            comms.add(load(zae));
          }
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

  /*
   * (non-Javadoc)
   * @see java.lang.AutoCloseable#close()
   */
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

  private class ExtensionFinder {
    private final List<String> extensions = Arrays.asList(new String[] {"comm", "concrete"});

    public String getExtension(ZipFile zf) {
      for (Enumeration<ZipArchiveEntry> e = zf.getEntries(); e.hasMoreElements();) {
        ZipArchiveEntry zae = e.nextElement();
        for (String extension : extensions) {
          if (zae.getName().endsWith(extension)) {
            return extension;
          }
        }
      }
      return null;
    }
  }
}
