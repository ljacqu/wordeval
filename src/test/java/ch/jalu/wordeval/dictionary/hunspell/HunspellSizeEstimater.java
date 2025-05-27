package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.DataUtils;
import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.config.SpringContainedRunner;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.DictionaryService;
import ch.jalu.wordeval.dictionary.HunspellDictionary;
import ch.jalu.wordeval.dictionary.hunspell.lineprocessor.HunspellLineProcessor;
import ch.jalu.wordeval.dictionary.hunspell.lineprocessor.RootAndAffixes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Estimates the number of total entries of a Hunspell dictionary.
 */
@Slf4j
public class HunspellSizeEstimater extends SpringContainedRunner {

  @Autowired
  private AppData appData;

  @Autowired
  private DictionaryService dictionaryService;

  @Autowired
  private HunspellDictionaryService hunspellDictionaryService;

  @Autowired
  private DataUtils dataUtils;

  public static void main(String[] args) {
    runApplication(HunspellSizeEstimater.class, args);
  }

  @Override
  public void run(String... args) {
    try (Scanner scanner = new Scanner(System.in)) {
      log.info("Language code: ");
      String code = scanner.nextLine().trim();

      Dictionary dictionary = appData.getDictionary(code);
      long numberOfEntries = switch (dictionary) {
        case HunspellDictionary hunDict -> estimateTotalEntries(hunDict);
      };

      log.info("Dictionary '{}' is estimated to have total entries: {}", code, numberOfEntries);

      log.info("Check how much there actually are? [y/n]");
      String checkActualSize = scanner.nextLine().trim();
      if ("y".equalsIgnoreCase(checkActualSize)) {
        int actualCount = dictionaryService.readAllWords(dictionary).size();
        log.info("Actual size ({}): {}", code, actualCount);
      }
    }
  }

  private long estimateTotalEntries(HunspellDictionary dictionary) {
    HunspellAffixes affixes = hunspellDictionaryService.loadAndParseAffixes(dictionary);
    HunspellLineProcessor lineProcessor = dictionary.getLineProcessor();

    try (Stream<String> lines = dataUtils.lines(dictionary.getFile())) {
      return lines.mapToLong(line -> {
          RootAndAffixes rootAndAffixes = lineProcessor.split(line);
          return estimateNumbersForLine(rootAndAffixes, affixes);
        })
        .sum();
    }
  }

  private long estimateNumbersForLine(RootAndAffixes rootAndAffixes, HunspellAffixes affixes) {
    if (rootAndAffixes.isEmpty()) {
      return 0;
    } else if (rootAndAffixes.affixFlags().isEmpty()) {
      return 1;
    }

    List<String> affixFlags = affixes.getFlagType().split(rootAndAffixes.affixFlags());
    return affixFlags.stream()
        .flatMap(flag -> affixes.streamThroughMatchingRules(rootAndAffixes.root(), flag))
        .mapToLong(rule -> {
          long estimatedEntries = 1;
          if (rule.isCrossProduct() && rule.getType() == AffixType.PFX) {
            estimatedEntries += affixFlags.stream()
                .flatMap(flag -> affixes.getAffixRulesByFlag().get(flag).stream())
                .filter(rule2 -> rule2.getType() == AffixType.SFX
                                            && rule2.isCrossProduct()
                                            && rule2.matches(rootAndAffixes.root()))
                .count();
          }
          estimatedEntries += rule.getContinuationClasses().stream()
              .filter(flag -> !affixes.getAffixRulesByFlag().get(flag).isEmpty())
              .count();
          return estimatedEntries;
        })
        .sum();
  }
}
