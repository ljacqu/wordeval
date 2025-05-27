package ch.jalu.wordeval.dictionary.hunspell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum AffixFlagType {

  /** Affix rules have a single character as flag name. */
  SINGLE,

  /** 2-character flag names are used, e.g. foo/Y1Z3F? -> affixes Y1, Z3, F?. */
  LONG,

  /** Numbers are used as flag names and are separated by commas. */
  NUMBER;

  /**
   * Returns the affix flag type that corresponds to the flag name. Throws an exception
   * if the value is unknown.
   *
   * @param str the name of the flag as defined in an .aff file
   * @return the corresponding flag type
   */
  public static AffixFlagType fromAffixFileString(String str) {
    return switch (str) {
      case "UTF-8" -> SINGLE;
      case "long" -> LONG;
      case "num" -> NUMBER;
      default -> throw new IllegalArgumentException("Unknown affix flag type: " + str);
    };
  }

  /**
   * Takes a string of specified affixes (from a .dic file) and splits it into individual flags.
   *
   * @param affixList the string list to split
   * @return the affix flags from the list
   */
  public List<String> split(String affixList) {
    if (this == SINGLE) {
      return affixList.chars()
          .mapToObj(i -> String.valueOf((char) i))
          .toList();
    } else if (this == LONG) {
      List<String> affixes = new ArrayList<>(affixList.length() / 2);
      for (int i = 0; i < affixList.length(); i += 2) {
        affixes.add(affixList.substring(i, i + 2));
      }
      return affixes;
    } else if (this == NUMBER) {
      return Arrays.asList(affixList.split(","));
    } else {
      throw new IllegalStateException("Unsupported affix type: " + this);
    }
  }
}
