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

  public Stream<String> loadAllWords(Stream<String> lines, HunspellDictionary dictionary) {
    HunspellAffixes affixDefinition = loadAndParseAffixes(dictionary);
    HunspellSanitizer sanitizer = dictionary.getSanitizer();

    return lines
        .map(sanitizer::split)
        .filter(baseWord -> !baseWord.isEmpty())
        .flatMap(baseWord -> unmuncherService.unmunch(baseWord, affixDefinition))
        .map(sanitizer::transform);
  }

  private HunspellAffixes loadAndParseAffixes(HunspellDictionary dictionary) {
    ParsedAffixes parsedAffixes;
    try (Stream<String> affixLines = dataUtils.lines(dictionary.getAffixFile())) {
      parsedAffixes = affixesParser.parseAffFile(affixLines);
    }
    return parserToModelConverter.convert(parsedAffixes);
  }
}
