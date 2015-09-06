package ch.ljacqu.wordeval.language;

public class HuSanitizer extends Sanitizer {

  private boolean skipWords = false;

  public HuSanitizer(char... delimiters) {
    super("hu", delimiters, new String[0], new String[0]);
  }

  @Override
  protected String customSanitize(String word) {
    if (word.isEmpty()) {
      return "";
    }

    // Skip Roman numerals; they are in range:
    // "xxxviii." to "v.", "lxxxviii." to "l."
    // "ix." to "i.", "cxxxviii." to "cv."
    // "clxxxviii." to "c."
    if (skipWords && wordEqualsOne(word, "v.", "l.", "i.", "cv.", "c.")) {
      skipWords = false;
      return "";
    } else if (wordEqualsOne(word, "xxxviii.", "lxxxviii.", "ix.", "cxxxviii.",
        "clxxxviii.")) {
      skipWords = true;
      return "";
    }

    // From ATP-vé to the end of the file, only abbreviation-like words are
    // present and number/Greek stuff we don't mind skipping (e.g. 20.-kal,
    // ‰-nyi, Φ-vé).
    if (word.equals("ATP-vé")) {
      skipWords = true;
    }
    if (skipWords) {
      return "";
    }

    // The dictionary contains a lot of odd entries like "góóóóól";
    // they're the only ones where "óóó" appears so we skip those
    if (word.indexOf("óóól") > -1) {
      return "";
    }

    // Some words have a starting '|' for some reason
    if (word.charAt(0) == '|') {
      return word.substring(1);
    }

    // Skip some chemical words because they have parentheses, which is annoying
    if (containsPart(word, "(vinil", "(izobutilén)", "(akril", "(metil")) {
      return "";
    }

    // Skip all entries with a period (.) as they really are only abbreviations
    // we are not really interested in. Some entries also use other odd symbols
    // that aren't very natural words, so skip those too
    if (containsPart(word, ".", "+", "±", "ø", "ʻ", "’", "­")) {
      return "";
    }

    if (wordEqualsOne(word, "[", "]", "{", "}", "#")) {
      return "";
    }

    // alakú, közben, közbeni, szer exist as own entry, so we can just return
    // the first word. vége and módra are not present alone in the dictionary,
    // so we take two other entries and re-appropriate them to send them.
    String foundPart = getFirstFound(word, " alak", " kor", " közben",
        " módra", " szer", " vég", " vége", " végén", " végi", " vevő");
    if (foundPart != null) {
      if (wordEqualsOne(word, "hó vége", "papagáj módra", "tél végi")) {
        // Return the second word in these exceptional cases
        return word.substring(word.indexOf(" ") + 1);
      }
      return word.substring(0, word.indexOf(foundPart));
    }

    // A few entries are "fő" with another word; fő exists alone while some
    // second words do not, so just return the second word in those cases
    if (containsPart(word, "fő ")) {
      return word.substring(3);
    } else if (word.equals("úti cél")) {
      // úti doesn't exist alone and cél is covered by "fő cél"
      return "úti";
    }

    // Remove yahoo! and dog breeds causing trouble
    // Csak azért is are all in the dictionary individually so let's skip it
    if (containsPart(word, "yahoo!", "Yahoo!")
        || wordEqualsOne(word, "lhasa apso", "yorkshire terrier",
            "csak azért is", "papír zsebkendő", "nota bene")) {
      return "";
    }

    // "üzembehelyezés-" and "üzembe helyezés" are in the dictionary but not the
    // individual words. As a not so nice hack we'll use üzembe and we'll
    // replace the entry "ë" (which has /nothing/ to do in a Hungarian
    // dictionary) with "helyeszés"
    if (word.equals("üzembe helyezés")) {
      return "üzembe";
    } else if (word.equals("ë")) {
      return "helyezés";
    } else if (word.equals("működő tőke")) {
      return "tőke";
    }

    if (word.equals("adieu[ph:agyő]")) {
      return "adieu";
    } else if (word.equals("ancien}")) {
      return "ancien";
    }

    return word;
  }

  private boolean containsPart(String word, String... parts) {
    return getFirstFound(word, parts) != null;
  }

  private String getFirstFound(String word, String... parts) {
    for (String part : parts) {
      if (word.indexOf(part) != -1) {
        return part;
      }
    }
    return null;
  }

  private boolean wordEqualsOne(String word, String... parts) {
    for (String part : parts) {
      if (word.equals(part)) {
        return true;
      }
    }
    return false;
  }

}
