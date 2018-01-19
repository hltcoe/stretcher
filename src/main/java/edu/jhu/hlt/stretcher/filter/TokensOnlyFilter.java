/*
 * Copyright 2012-2018 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.stretcher.filter;

import com.typesafe.config.Config;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.Tokenization;;

/**
 * Leaves behind the text, section and sentence structure, and the tokenization.
 * Removes taggings, links, and parses.
 * See the code for a fuller list of what it removes.
 */
public class TokensOnlyFilter implements Filter {

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.filter.Filter#initialize(com.typesafe.config.Config)
   */
  @Override
  public void initialize(Config config) {}

  /*
   * (non-Javadoc)
   * @see edu.jhu.hlt.stretcher.filter.Filter#filter(edu.jhu.hlt.concrete.Communication)
   */
  @Override
  public void filter(Communication c) {
    c.unsetCommunicationTaggingList();
    c.unsetEntityMentionSetList();
    c.unsetEntitySetList();
    c.unsetSituationMentionSetList();
    c.unsetSituationSetList();
    c.unsetKeyValueMap();
    c.unsetCommunicationMetadata();
    for (Section section : c.getSectionList()) {
      for (Sentence sentence : section.getSentenceList()) {
        Tokenization t = sentence.getTokenization();
        t.unsetDependencyParseList();
        t.unsetParseList();
        t.unsetTokenTaggingList();
        t.unsetSpanLinkList();
      }
    }
  }

}
