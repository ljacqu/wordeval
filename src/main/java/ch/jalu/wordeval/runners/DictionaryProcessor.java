package ch.jalu.wordeval.runners;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.dictionary.WordFactory;
import ch.jalu.wordeval.dictionary.sanitizer.Sanitizer;
import ch.jalu.wordeval.language.Language;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Processes a dictionary.
 */
public final class DictionaryProcessor {

  private DictionaryProcessor() {
  }

  public static List<Word> readAllWords(Dictionary dictionary) {
    final Sanitizer sanitizer = dictionary.buildSanitizer();
    final Language language = dictionary.getLanguage();
    final WordFactory wordFactory = new WordFactory(language);

    return DataUtils.readAllLines(dictionary.getFile())
        .stream()
        .map(sanitizer::isolateWord)
        .filter(StringUtils::isNotEmpty)
        .map(wordFactory::createWordObject)
        .toList();
  }

  public static WordEntries getSkippedLines(Dictionary dictionary) {
    final Sanitizer sanitizer = dictionary.buildSanitizer();
    List<String> skippedLines = new ArrayList<>();
    List<String> includedLines = new ArrayList<>();

    DataUtils.readAllLines(dictionary.getFile()).forEach(line -> {
      String isolatedWord = sanitizer.isolateWord(line);
      if (StringUtils.isEmpty(isolatedWord)) {
        skippedLines.add(line);
      } else {
        includedLines.add(isolatedWord + " -> " + line);
      }
    });

    return new WordEntries(skippedLines, includedLines);
  }

  public record WordEntries(List<String> skippedLines, List<String> includedLines) {
  }
}
