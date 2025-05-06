package ch.jalu.wordeval.dictionary.hunspell.sanitizer;

import static org.apache.commons.lang3.StringUtils.containsAny;

/**
 * Custom sanitizer implementing specific rules for the Hungarian dictionary.
 */
public class HuSanitizer extends HunspellSanitizer {

  private boolean skipWords = false;

  /**
   * Creates a new instance of a sanitizer for the Hungarian dictionary.
   */
  public HuSanitizer() {
    super(getSkipSequences());
  }

  @Override
  public RootAndAffixes split(String line) {
    RootAndAffixes rootAndAffixes = super.split(line);
    return remapWordIfNeeded(rootAndAffixes);
  }

  public RootAndAffixes remapWordIfNeeded(RootAndAffixes rootAndAffixes) {
    if (rootAndAffixes.isEmpty()) {
      return rootAndAffixes;
    }

    String word = rootAndAffixes.root();
    // Skip Roman numerals; they are in range:
    // "xxxviii." to "v.", "lxxxviii." to "l."
    // "ix." to "i.", "cxxxviii." to "cv."
    // "clxxxviii." to "c."
    if (skipWords && equalsAny(word, "v.", "l.", "i.", "cv.", "c.")) {
      skipWords = false;
      return RootAndAffixes.EMPTY;
    } else if (equalsAny(word, "xxxviii.", "lxxxviii.", "ix.", "cxxxviii.", "clxxxviii.")) {
      skipWords = true;
      return RootAndAffixes.EMPTY;
    }

    // From ATP-vé to the end of the file, only abbreviation-like words are
    // present and number/Greek stuff we don't mind skipping (e.g. 20.-kal,
    // ‰-nyi, Φ-vé).
    if ("ATP-vé".equals(word)) {
      skipWords = true;
    }
    if (skipWords) {
      return RootAndAffixes.EMPTY;
    }

    // The dictionary contains a lot of odd entries like "góóóóól",
    // which are the only ones where "óóó" appears, so we skip those
    if (word.contains("óóól")) {
      return RootAndAffixes.EMPTY;
    }

    // Some words have a starting '|' for some reason
    if (word.charAt(0) == '|') {
      return rootAndAffixes.withNewRoot(word.substring(1));
    }

    // Skip some chemical words because they have parentheses, which is annoying
    if (containsAny(word, "(vinil", "(izobutilén)", "(akril", "(metil")) {
      return RootAndAffixes.EMPTY;
    }

    if (equalsAny(word, "[", "]", "{", "}", "#")) {
      return RootAndAffixes.EMPTY;
    }

    // alakú, közben, közbeni, szer exist as own entry, so we can just return
    // the first word. vége and módra are not present alone in the dictionary,
    // so we take two other entries and re-appropriate them to send them.
    String foundPart = getFirstContains(word, " alak", " kor", " közben",
        " módra", " szer", " vég", " vége", " végén", " végi", " vevő");
    if (foundPart != null) {
      if (equalsAny(word, "hó vége", "papagáj módra", "tél végi")) {
        // Return the second word in these exceptional cases
        return rootAndAffixes.withNewRoot(word.substring(word.indexOf(" ") + 1));
      }

      return rootAndAffixes.withNewRoot(word.substring(0, word.indexOf(foundPart)));
    }

    // A few entries are "fő" with another word; fő exists alone while some
    // second words do not, so just return the second word in those cases
    if (word.startsWith("fő ")) {
      return rootAndAffixes.withNewRoot(word.substring(3));
    } else if ("úti cél".equals(word)) {
      // úti doesn't exist alone and cél is covered by "fő cél"
      return rootAndAffixes.withNewRoot("úti");
    }

    // Remove yahoo! and dog breeds causing trouble
    // Csak azért is are all in the dictionary individually so let's skip it
    if (containsAny(word, "yahoo!", "Yahoo!")
        || equalsAny(word, "lhasa apso", "yorkshire terrier",
            "csak azért is", "papír zsebkendő", "nota bene")) {
      return RootAndAffixes.EMPTY;
    }

    // TODO: Some of those remappings are probably not correct -- cannot keep the same affix flags if the word changes!

    // "üzembehelyezés-" and "üzembe helyezés" are in the dictionary but not the
    // individual words. As a not so nice hack we'll use üzembe and we'll
    // replace the entry "ë" (which has /nothing/ to do in a Hungarian
    // dictionary) with "helyeszés"
    if ("üzembe helyezés".equals(word)) {
      return rootAndAffixes.withNewRoot("üzembe");
    } else if ("ë".equals(word)) {
      return rootAndAffixes.withNewRoot("helyezés");
    } else if ("működő tőke".equals(word)) {
      return rootAndAffixes.withNewRoot("tőke");
    }

    if ("adieu[ph:agyő]".equals(word)) {
      return rootAndAffixes.withNewRoot("adieu");
    } else if ("ancien}".equals(word)) {
      return rootAndAffixes.withNewRoot("ancien");
    }

    return rootAndAffixes;
  }

  private static String getFirstContains(String word, String... parts) {
    for (String part : parts) {
      if (word.contains(part)) {
        return part;
      }
    }
    return null;
  }

  private static boolean equalsAny(String word, String... parts) {
    for (String part : parts) {
      if (word.equals(part)) {
        return true;
      }
    }
    return false;
  }

  private static String[] getSkipSequences() {
    return new String[]{".", "+", "±", "ø", "ʻ", "’", "­"};
  }
}
