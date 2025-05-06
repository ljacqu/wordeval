package ch.jalu.wordeval.dictionary.hunspell.sanitizer;

/**
 * Parsed Hunspell dictionary line representing a root and affix flags.
 *
 * @param root base word (may be empty, never null)
 * @param affixFlags affix flags to apply (may be empty, never null)
 */
public record RootAndAffixes(String root, String affixFlags) {

  public static final RootAndAffixes EMPTY = new RootAndAffixes("", "");

  public boolean isEmpty() {
    return root.isEmpty();
  }

  public RootAndAffixes withNewRoot(String root) {
    return new RootAndAffixes(root, affixFlags);
  }
}
