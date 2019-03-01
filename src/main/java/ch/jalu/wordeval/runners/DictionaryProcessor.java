package ch.jalu.wordeval.runners;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.dictionary.WordFactory;
import ch.jalu.wordeval.dictionary.sanitizer.Sanitizer;
import ch.jalu.wordeval.language.Language;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Processes a dictionary.
 */
public final class DictionaryProcessor {

  private DictionaryProcessor() {
  }

  public static Collection<Word> readAllWords(Dictionary dictionary) {
    final Sanitizer sanitizer = dictionary.buildSanitizer();
    final Language language = dictionary.getLanguage();
    final WordFactory wordFactory = new WordFactory(language);

    return DataUtils.readAllLines(dictionary.getFile())
      .stream()
      .map(sanitizer::isolateWord)
      .filter(StringUtils::isNotEmpty)
      .map(wordFactory::createWordObject)
      .collect(Collectors.toList());
  }
}
