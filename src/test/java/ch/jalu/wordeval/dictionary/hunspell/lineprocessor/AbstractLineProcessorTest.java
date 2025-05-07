package ch.jalu.wordeval.dictionary.hunspell.lineprocessor;

import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.config.BaseConfiguration;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.DictionaryService;
import ch.jalu.wordeval.dictionary.HunspellDictionary;
import ch.jalu.wordeval.dictionary.hunspell.AffixFlagType;
import ch.jalu.wordeval.dictionary.hunspell.HunspellAffixes;
import ch.jalu.wordeval.dictionary.hunspell.HunspellDictionaryService;
import com.google.common.collect.ArrayListMultimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Common type for dictionary Hunspell line processor tests.
 */
@SpringJUnitConfig(classes = BaseConfiguration.class)
abstract class AbstractLineProcessorTest {

  @Autowired
  private AppData appData;

  @Autowired
  protected DictionaryService dictionaryService;

  @Autowired
  private HunspellDictionaryService hunspellDictionaryService;

  /**
   * Gets the dictionary with the given code. Throws an exception if it does not exist.
   *
   * @param code the language code to look up
   * @return the specified dictionary
   */
  protected HunspellDictionary getDictionary(String code) {
    return (HunspellDictionary) appData.getDictionary(code);
  }

  /**
   * Throws a JUnit assumption exception if the file the dictionary object points to does not exist.
   *
   * @param dictionary the dictionary whose file should be checked
   */
  protected static void assumeDictionaryFileExists(Dictionary dictionary) {
    assumeTrue(TestUtil.doesDictionaryFileExist(dictionary),
        () -> "Skipping test because the dictionary file doesn't exist");
  }

  /**
   * Processes the given lines and returns the words as defined by the line processor.
   *
   * @param lines the lines to process
   * @param lineProcessor the line processor to use on the lines
   * @return the resulting words
   */
  protected List<String> processLines(List<String> lines, HunspellLineProcessor lineProcessor) {
    HunspellAffixes affixDefinition = createEmptyAffixDefinition();
    return hunspellDictionaryService.loadAllWords(lines.stream(), lineProcessor, affixDefinition)
        .toList();
  }

  /**
   * @return minimal affixes definition object
   */
  private static HunspellAffixes createEmptyAffixDefinition() {
    HunspellAffixes affixDefinition = new HunspellAffixes();
    affixDefinition.setFlagType(AffixFlagType.SINGLE);
    affixDefinition.setAffixRulesByFlag(ArrayListMultimap.create());
    return affixDefinition;
  }
}
