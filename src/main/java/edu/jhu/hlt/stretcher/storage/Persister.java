/*
 * Copyright 2012-2017 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.storage;

import edu.jhu.hlt.concrete.Communication;

public interface Persister extends AutoCloseable {
  void store(Communication c);
}