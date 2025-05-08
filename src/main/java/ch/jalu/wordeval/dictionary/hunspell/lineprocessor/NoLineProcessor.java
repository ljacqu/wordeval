package ch.jalu.wordeval.dictionary.hunspell.lineprocessor;

/**
 * Line processor for the Norwegian (Bokmål) and Norwegian (Nynorsk) dictionaries.
 */
public class NoLineProcessor extends HunspellLineProcessor {

  public NoLineProcessor() {
    super(".");
  }

  @Override
  public RootAndAffixes splitWithoutValidation(String line) {
    // Some entries like aggresjon/ADFJ\ have a backslash as affix, which seems not to do anything. The affixes file
    // has some affix classes commented out with special chars as flag name...
    return super.splitWithoutValidation(line.replace("\\", ""));
  }
}
