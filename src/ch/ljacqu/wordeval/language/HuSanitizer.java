package ch.ljacqu.wordeval.language;

import java.util.Locale;

class HuSanitizer extends Sanitizer {

  private boolean skipTillEnd = false;

  HuSanitizer(Locale locale, char... delimiters) {
    super(locale, delimiters);
  }

  HuSanitizer(char... delimiters) {
    super("hu", delimiters);
  }

  @Override
  protected String customSanitize(String word) {
    // From ATP-vé to the end of the file, only abbreviation-like words are
    // present and number/Greek stuff we don't mind skipping (e.g. 20.-kal,
    // ‰-nyi, Φ-vé).
    if (word.equals("ATP-vé")) {
      skipTillEnd = true;
    }
    if (skipTillEnd) {
      return "";
    }

    // The dictionary contains a lot of odd entries like "góóóóól";
    // they're the only ones where "óóó" appears so we skip those
    if (word.indexOf("óóól") > -1) {
      return "";
    }

    // Skip some chemical words because they have parentheses, which is annoying
    if (containsPart(word, "(vinil", "(izobutilén)", "(akril", "(metil")) {
      return "";
    }
    
    if (word.equals("[") || word.equals("]")) {
      return "";
    }

    // alakú, közben, közbeni, szer exist as own entry, so we can just return
    // the first word. vége and módra are not present alone in the dictionary,
    // so we take two other entries and re-appropriate them to send them.
    String foundPart = getFirstFound(word, " alak", " kor", " közben",
        " módra", " szer", " vég", " vége", " végén", " végi", " vevő");
    if (!foundPart.isEmpty()) {
      if (word.equals("hó vége") || word.equals("papagáj módra")
          || word.equals("tél végi")) {
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
    if (containsPart(word, "yahoo!", "Yahoo!") || word.equals("lhasa apso")
        || word.equals("yorkshire terrier") || word.equals("csak azért is")
        || word.equals("papír zsebkendő") || word.equals("nota bene")) {
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
    }

    return word;
  }

  private boolean containsPart(String word, String... parts) {
    return !getFirstFound(word, parts).isEmpty();
  }

  private String getFirstFound(String word, String... parts) {
    for (String part : parts) {
      if (word.indexOf(part) != -1) {
        return part;
      }
    }
    return "";
  }

}
