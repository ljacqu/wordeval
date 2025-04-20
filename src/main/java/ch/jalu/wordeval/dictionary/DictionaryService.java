package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.dictionary.sanitizer.Sanitizer;
import ch.jalu.wordeval.language.Language;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for processing dictionaries.
 */
@Service
public class DictionaryService {

  @Autowired
  private DataUtils dataUtils;

  DictionaryService() {
  }

  /**
   * Reads all words from the dictionary's file and sanitizes them.
   *
   * @param dictionary the dictionary to load
   * @return all words of the dictionary
   */
  public List<Word> readAllWords(Dictionary dictionary) {
    return processAllWords(dictionary, dataUtils.readAllLines(dictionary.getFile()));
  }

  public List<Word> processAllWords(Dictionary dictionary, List<String> lines) {
    final Sanitizer sanitizer = dictionary.buildSanitizer();
    final Language language = dictionary.getLanguage();
    final WordFactory wordFactory = new WordFactory(language);

    return lines.stream()
        .map(sanitizer::isolateWord)
        .filter(StringUtils::isNotEmpty)
        .map(wordFactory::createWordObject)
        .toList();
  }

  public WordEntries processWordsForDebug(Dictionary dictionary) {
    final Sanitizer sanitizer = dictionary.buildSanitizer();
    List<String> skippedLines = new ArrayList<>();
    List<String> includedLines = new ArrayList<>();

    dataUtils.readAllLines(dictionary.getFile()).forEach(line -> {
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
