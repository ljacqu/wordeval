package ch.jalu.wordeval.dictionary.hunspell.sanitizer;

import org.apache.commons.lang3.StringUtils;

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
  public boolean skipLine(String word) {
    if (StringUtils.isEmpty(word)) {
      return true;
    }

    // Skip Roman numerals; they are in range:
    // "xxxviii." to "v.", "lxxxviii." to "l."
    // "ix." to "i.", "cxxxviii." to "cv."
    // "clxxxviii." to "c."
    if (skipWords && equalsAny(word, "v.", "l.", "i.", "cv.", "c.")) {
      skipWords = false;
      return true;
    } else if (equalsAny(word, "xxxviii.", "lxxxviii.", "ix.", "cxxxviii.", "clxxxviii.")) {
      skipWords = true;
      return true;
    }

    // From ATP-vé to the end of the file, only abbreviation-like words are
    // present and number/Greek stuff we don't mind skipping (e.g. 20.-kal,
    // ‰-nyi, Φ-vé).
    if ("ATP-vé".equals(word)) {
      skipWords = true;
    }
    if (skipWords) {
      return true;
    }

    // The dictionary contains a lot of odd entries like "góóóóól",
    // which are the only ones where "óóó" appears, so we skip those
    if (word.contains("óóól")) {
      return true;
    }

    // Some words have a starting '|' for some reason
    if (word.charAt(0) == '|') {
      // TODO return word.substring(1);
      return false;
    }

    // Skip some chemical words because they have parentheses, which is annoying
    if (containsAny(word, "(vinil", "(izobutilén)", "(akril", "(metil")) {
      return true;
    }

    if (equalsAny(word, "[", "]", "{", "}", "#")) {
      return true;
    }

    // alakú, közben, közbeni, szer exist as own entry, so we can just return
    // the first word. vége and módra are not present alone in the dictionary,
    // so we take two other entries and re-appropriate them to send them.
    String foundPart = getFirstContains(word, " alak", " kor", " közben",
        " módra", " szer", " vég", " vége", " végén", " végi", " vevő");
    if (foundPart != null) {
      if (equalsAny(word, "hó vége", "papagáj módra", "tél végi")) {
        // Return the second word in these exceptional cases
        // TODO: How do we deal with this???
        // return word.substring(word.indexOf(" ") + 1);
        return false;
      }

      // TODO: Sanitizer transforms roots?
      // return word.substring(0, word.indexOf(foundPart));
      return false;
    }

    // A few entries are "fő" with another word; fő exists alone while some
    // second words do not, so just return the second word in those cases
    if (word.startsWith("fő ")) {
      // TODO: return word.substring(3);
      return false;
    } else if ("úti cél".equals(word)) {
      // úti doesn't exist alone and cél is covered by "fő cél"
      // TODO: return "úti";
      return false;
    }

    // Remove yahoo! and dog breeds causing trouble
    // Csak azért is are all in the dictionary individually so let's skip it
    if (containsAny(word, "yahoo!", "Yahoo!")
        || equalsAny(word, "lhasa apso", "yorkshire terrier",
            "csak azért is", "papír zsebkendő", "nota bene")) {
      return true;
    }

    // "üzembehelyezés-" and "üzembe helyezés" are in the dictionary but not the
    // individual words. As a not so nice hack we'll use üzembe and we'll
    // replace the entry "ë" (which has /nothing/ to do in a Hungarian
    // dictionary) with "helyeszés"
    if ("üzembe helyezés".equals(word)) {
      // TODO: return "üzembe";
      return false;
    } else if ("ë".equals(word)) {
      // TODO: return "helyezés";
      return false;
    } else if ("működő tőke".equals(word)) {
      // TODO: return "tőke";
      return false;
    }

    if ("adieu[ph:agyő]".equals(word)) {
      // TODO: return "adieu";
      return false;
    } else if ("ancien}".equals(word)) {
      // TODO: return "ancien";
      return false;
    }

    return false;
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
