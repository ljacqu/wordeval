package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.dictionary.HunspellDictionary;
import ch.jalu.wordeval.dictionary.hunspell.parser.AffixesParser;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixes;
import ch.jalu.wordeval.dictionary.hunspell.sanitizer.HunspellSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class HunspellDictionaryService {

  @Autowired
  private AffixesParser affixesParser;

  @Autowired
  private ParserToModelConverter parserToModelConverter;

  @Autowired
  private HunspellUnmuncherService unmuncherService;

  @Autowired
  private DataUtils dataUtils;

  HunspellDictionaryService() {
  }

  /**
   * Returns a stream with all words defined by the given dictionary.
   *
   * @param lines the lines of the dictionary to read from
   * @param dictionary the dictionary the lines belong to
   * @return all parsed and unmunched words
   */
  public Stream<String> loadAllWords(Stream<String> lines, HunspellDictionary dictionary) {
    HunspellAffixes affixDefinition = loadAndParseAffixes(dictionary);
    HunspellSanitizer sanitizer = dictionary.getSanitizer();

    return lines
        .map(sanitizer::split)
        .filter(baseWord -> !baseWord.isEmpty())
        .flatMap(baseWord -> unmuncherService.unmunch(baseWord, affixDefinition))
        .map(sanitizer::transform);
  }

  /**
   * Loads all affix rules defined by the given dictionary.
   *
   * @param dictionary the dictionary to process
   * @return the dictionary's affix rules
   */
  public HunspellAffixes loadAndParseAffixes(HunspellDictionary dictionary) {
    ParsedAffixes parsedAffixes;
    try (Stream<String> affixLines = dataUtils.lines(dictionary.getAffixFile())) {
      parsedAffixes = affixesParser.parseAffFile(affixLines);
    }
    return parserToModelConverter.convert(parsedAffixes);
  }
}
