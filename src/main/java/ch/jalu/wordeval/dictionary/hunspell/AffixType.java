package ch.jalu.wordeval.dictionary.hunspell;

public enum AffixType {

  /** Prefix. */
  PFX,

  /** Suffix. */
  SFX;

  public static AffixType fromString(String value) {
    return switch (value) {
      case "PFX" -> PFX;
      case "SFX" -> SFX;
      default -> throw new IllegalArgumentException("Invalid affix type: " + value);
    };
  }
}
