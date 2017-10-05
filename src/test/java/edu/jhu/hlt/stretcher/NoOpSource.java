package edu.jhu.hlt.stretcher;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.stretcher.source.CommunicationSource;

public class NoOpSource implements CommunicationSource {

  @Override
  public boolean exists(String id) {
    return false;
  }

  @Override
  public Optional<Communication> get(String id) {
    return Optional.empty();
  }

  @Override
  public List<Communication> get(long offset, long nToGet) {
    return ImmutableList.of();
  }

  @Override
  public List<Communication> get(List<String> ids) {
    return ImmutableList.of();
  }

  @Override
  public int size() {
    return 0;
  }
}
