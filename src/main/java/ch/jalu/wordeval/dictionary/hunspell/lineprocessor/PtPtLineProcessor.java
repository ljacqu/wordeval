package ch.jalu.wordeval.dictionary.hunspell.lineprocessor;

/**
 * Line processor for the Portuguese (pt-pt) dictionary.
 */
public class PtPtLineProcessor extends HunspellLineProcessor {

  @Override
  public RootAndAffixes splitWithoutValidation(String line) {
    if (line.contains("[CAT=punct") && !line.contains("ABR=1")) {
      return RootAndAffixes.EMPTY;
    }

    RootAndAffixes rootAndAffixes = super.splitWithoutValidation(line);
    if (rootAndAffixes.affixFlags().isEmpty()) {
      int indexOfTab = line.indexOf('\t');
      if (indexOfTab >= 0) {
        return new RootAndAffixes(line.substring(0, indexOfTab).trim(), "");
      }
    }
    return rootAndAffixes;
  }
}
