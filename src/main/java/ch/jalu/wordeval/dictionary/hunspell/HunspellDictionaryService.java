package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.dictionary.HunspellDictionary;
import ch.jalu.wordeval.dictionary.hunspell.lineprocessor.HunspellLineProcessor;
import ch.jalu.wordeval.dictionary.hunspell.parser.AffixesParser;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * Service for Hunspell dictionaries.
 */
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
    HunspellLineProcessor lineProcessor = dictionary.getLineProcessor();
    HunspellAffixes affixDefinition = loadAndParseAffixes(dictionary);
    return loadAllWords(lines, lineProcessor, affixDefinition);
  }

  /**
   * Returns a stream with all words processed by the given lines.
   *
   * @param lines the lines of the dictionary to read from
   * @param lineProcessor line processor for splitting/handling lines
   * @param affixDefinition definition of the Hunspell dictionary's affixes
   * @return all parsed and unmunched words
   */
  public Stream<String> loadAllWords(Stream<String> lines, HunspellLineProcessor lineProcessor,
                                     HunspellAffixes affixDefinition) {
    return lines
        .map(lineProcessor::split)
        .filter(baseWord -> !baseWord.isEmpty())
        .flatMap(baseWord -> unmuncherService.unmunch(baseWord, affixDefinition))
        .map(lineProcessor::transform);
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
