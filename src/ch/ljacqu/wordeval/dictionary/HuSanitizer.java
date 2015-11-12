package ch.ljacqu.wordeval.dictionary;

import static org.apache.commons.lang3.StringUtils.containsAny;

import org.apache.commons.lang3.StringUtils;

import ch.ljacqu.wordeval.language.Language;

/**
 * Custom sanitizer implementing specific rules for the Hungarian dictionary.
 */
public class HuSanitizer extends Sanitizer {

  private boolean skipWords = false;

  /**
   * Creates a new instance of a sanitizer for the Hungarian dictionary.
   */
  public HuSanitizer() {
    super(Language.get("hu"), initSettings());
  }

  @Override
  protected String customSanitize(String word) {
    if (StringUtils.isEmpty(word)) {
      return "";
    }

    // Skip Roman numerals; they are in range:
    // "xxxviii." to "v.", "lxxxviii." to "l."
    // "ix." to "i.", "cxxxviii." to "cv."
    // "clxxxviii." to "c."
    if (skipWords && equalsAny(word, "v.", "l.", "i.", "cv.", "c.")) {
      skipWords = false;
      return "";
    } else if (equalsAny(word, "xxxviii.", "lxxxviii.", "ix.", "cxxxviii.", "clxxxviii.")) {
      skipWords = true;
      return "";
    }

    // From ATP-vé to the end of the file, only abbreviation-like words are
    // present and number/Greek stuff we don't mind skipping (e.g. 20.-kal,
    // ‰-nyi, Φ-vé).
    if ("ATP-vé".equals(word)) {
      skipWords = true;
    }
    if (skipWords) {
      return "";
    }

    // The dictionary contains a lot of odd entries like "góóóóól",
    // which are the only ones where "óóó" appears, so we skip those
    if (word.indexOf("óóól") > -1) {
      return "";
    }

    // Some words have a starting '|' for some reason
    if (word.charAt(0) == '|') {
      return word.substring(1);
    }

    // Skip some chemical words because they have parentheses, which is annoying
    if (containsAny(word, "(vinil", "(izobutilén)", "(akril", "(metil")) {
      return "";
    }

    if (equalsAny(word, "[", "]", "{", "}", "#")) {
      return "";
    }

    // alakú, közben, közbeni, szer exist as own entry, so we can just return
    // the first word. vége and módra are not present alone in the dictionary,
    // so we take two other entries and re-appropriate them to send them.
    String foundPart = getFirstContains(word, " alak", " kor", " közben",
        " módra", " szer", " vég", " vége", " végén", " végi", " vevő");
    if (foundPart != null) {
      if (equalsAny(word, "hó vége", "papagáj módra", "tél végi")) {
        // Return the second word in these exceptional cases
        return word.substring(word.indexOf(" ") + 1);
      }
      return word.substring(0, word.indexOf(foundPart));
    }

    // A few entries are "fő" with another word; fő exists alone while some
    // second words do not, so just return the second word in those cases
    if (word.startsWith("fő ")) {
      return word.substring(3);
    } else if ("úti cél".equals(word)) {
      // úti doesn't exist alone and cél is covered by "fő cél"
      return "úti";
    }

    // Remove yahoo! and dog breeds causing trouble
    // Csak azért is are all in the dictionary individually so let's skip it
    if (containsAny(word, "yahoo!", "Yahoo!")
        || equalsAny(word, "lhasa apso", "yorkshire terrier",
            "csak azért is", "papír zsebkendő", "nota bene")) {
      return "";
    }

    // "üzembehelyezés-" and "üzembe helyezés" are in the dictionary but not the
    // individual words. As a not so nice hack we'll use üzembe and we'll
    // replace the entry "ë" (which has /nothing/ to do in a Hungarian
    // dictionary) with "helyeszés"
    if ("üzembe helyezés".equals(word)) {
      return "üzembe";
    } else if ("ë".equals(word)) {
      return "helyezés";
    } else if ("működő tőke".equals(word)) {
      return "tőke";
    }

    if ("adieu[ph:agyő]".equals(word)) {
      return "adieu";
    } else if ("ancien}".equals(word)) {
      return "ancien";
    }

    return word;
  }

  private static String getFirstContains(String word, String... parts) {
    for (String part : parts) {
      if (word.indexOf(part) != -1) {
        return part;
      }
    }
    return null;
  }

  private static DictionarySettings initSettings() {
    return new DictionarySettings("hu").setDelimiters('/', '\t')
        .setSkipSequences(".", "+", "±", "ø", "ʻ", "’", "­");
  }

  private static boolean equalsAny(String word, String... parts) {
    for (String part : parts) {
      if (word.equals(part)) {
        return true;
      }
    }
    return false;
  }

}
