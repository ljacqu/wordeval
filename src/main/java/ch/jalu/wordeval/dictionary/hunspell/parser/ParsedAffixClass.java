package ch.jalu.wordeval.dictionary.hunspell.parser;

import ch.jalu.wordeval.dictionary.hunspell.AffixType;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class ParsedAffixClass {

  public AffixType type;
  /** The flag, i.e. identifier of this class. Examples: K, V, 1447, Lp. */
  public String flag;
  public boolean crossProduct;
  public final List<Rule> rules = new ArrayList<>();

  public record Rule(String strip, String affix, String condition) {
  }
}
