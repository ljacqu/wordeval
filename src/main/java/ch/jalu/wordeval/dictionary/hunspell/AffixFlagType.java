package ch.jalu.wordeval.dictionary.hunspell;

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
      case "long" -> LONG;
      case "num" -> NUMBER;
      default -> throw new IllegalArgumentException("Unknown affix flag type: " + str);
    };
  }
}
