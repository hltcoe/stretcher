package edu.jhu.hlt.stretcher.storage;

import edu.jhu.hlt.concrete.Communication;

public interface Persister extends AutoCloseable {
  void store(Communication c);
}