package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.dictionary.hunspell.HunspellDictionaryService;
import ch.jalu.wordeval.dictionary.hunspell.sanitizer.HunspellSanitizer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Service for processing dictionaries.
 */
@Service
public class DictionaryService {

  @Autowired
  private DataUtils dataUtils;

  @Autowired
  private HunspellDictionaryService hunspellDictionaryService;

  DictionaryService() {
  }

  /**
   * Reads all words from the dictionary's file and sanitizes them.
   *
   * @param dictionary the dictionary to load
   * @return all words of the dictionary
   */
  public List<Word> readAllWords(Dictionary dictionary) {
    WordFactory wordFactory = new WordFactory(dictionary.getLanguage());
    try (Stream<String> lines = dataUtils.lines(dictionary.getFile())) {
      return loadWords(lines, dictionary)
          .filter(StringUtils::isNotEmpty)
          .map(wordFactory::createWordObject)
          .toList();
    }
  }

  private Stream<String> loadWords(Stream<String> lines, Dictionary dictionary) {
    if (dictionary instanceof HunspellDictionary hunDict) {
      return hunspellDictionaryService.loadAllWords(lines, hunDict);
    }
    throw new IllegalStateException("Unsupported dictionary type: " + dictionary.getClass());
  }

  // todo: move this?
  public WordEntries processWordsForDebug(Dictionary dictionary) {
    if (dictionary instanceof HunspellDictionary hunDict) {
      HunspellSanitizer sanitizer = hunDict.getSanitizer();
      List<String> skippedLines = new ArrayList<>();
      List<String> includedLines = new ArrayList<>();

      dataUtils.readAllLines(dictionary.getFile()).forEach(line -> {
        if (sanitizer.skipLine(line)) {
          skippedLines.add(line);
        } else {
          includedLines.add(line);
        }
      });
      return new WordEntries(skippedLines, includedLines);
    }

    throw new IllegalStateException("Unsupported dictionary type: " + dictionary.getClass());
  }

  public record WordEntries(List<String> skippedLines, List<String> includedLines) {
  }
}
