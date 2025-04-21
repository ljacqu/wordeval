package ch.jalu.wordeval.dictionary.hunspell.parser;

import ch.jalu.wordeval.dictionary.hunspell.AffixType;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class ParsedRule {

  public AffixType type;
  public String flag;      // The flag name (e.g., K, V, N)
  public boolean crossProduct;
  public List<RuleEntry> rules = new ArrayList<>();

  @ToString
  public static class RuleEntry {

    public String strip;
    public String append;
    public String condition;

    public RuleEntry(String strip, String append, String condition) {
      this.strip = strip;
      this.append = append;
      this.condition = condition;
    }
  }
}
