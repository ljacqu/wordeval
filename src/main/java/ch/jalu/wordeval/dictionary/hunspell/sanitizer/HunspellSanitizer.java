package ch.jalu.wordeval.dictionary.hunspell.sanitizer;

import org.apache.commons.lang3.StringUtils;

public class HunspellSanitizer {

  private final String[] skipSequences;

  public HunspellSanitizer(String... skipSequences) {
    this.skipSequences = skipSequences;
  }

  public boolean skipLine(String line) {
    return line.isEmpty() || StringUtils.containsAny(line, skipSequences);
  }

  public String transform(String word) {
    return word;
  }
}
