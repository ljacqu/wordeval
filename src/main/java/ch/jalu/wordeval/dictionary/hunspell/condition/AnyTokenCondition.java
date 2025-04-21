package ch.jalu.wordeval.dictionary.hunspell.condition;

/**
 * Affix condition that is always true.
 */
public final class AnyTokenCondition implements AffixCondition {

  /** Singleton instance. */
  public static final AnyTokenCondition INSTANCE = new AnyTokenCondition();

  private AnyTokenCondition() {
  }

  @Override
  public boolean matches(String word) {
    return true;
  }
}
