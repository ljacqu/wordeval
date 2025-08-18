package ch.jalu.wordeval.dictionary.hunspell.parser;

import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.config.SpringContainedRunner;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.HunspellDictionary;
import ch.jalu.wordeval.dictionary.hunspell.AffixRule;
import ch.jalu.wordeval.dictionary.hunspell.HunspellAffixes;
import ch.jalu.wordeval.dictionary.hunspell.HunspellDictionaryService;
import com.google.common.collect.Multimaps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Scanner;

/**
 * Runner that prints out the parsed affix rules of a dictionary file.
 */
public class AffixesParserRunner extends SpringContainedRunner {

  @Autowired
  private AppData appData;

  @Autowired
  private HunspellDictionaryService hunspellDictionaryService;

  public static void main(String[] args) {
    runApplication(AffixesParserRunner.class, args);
  }

  @Override
  public void run(String... args) {
    try (Scanner scanner = new Scanner(System.in)) {
      System.out.println("Language code: ");
      String code = scanner.nextLine().trim();

      Dictionary dictionary = appData.getDictionary(code);
      HunspellAffixes affixes = switch (dictionary) {
        case HunspellDictionary hunDict -> hunspellDictionaryService.loadAndParseAffixes(hunDict);
      };

      System.out.println("Processed " + affixes.getAffixRulesByFlag().keySet().size() + " affix classes");
      Multimaps.asMap(affixes.getAffixRulesByFlag())
          .forEach(this::printAffixClass);
    }
  }

  private void printAffixClass(String flag, List<AffixRule> rules) {
    System.out.println(rules.getFirst().getType() + " " + flag);
    int cnt = 0;
    for (AffixRule rule : rules) {
      if (rule instanceof AffixRule.SuffixRule sfx) {
        System.out.println(" "
            + StringUtils.defaultIfEmpty(sfx.getStrip(), "0")
            + " " + StringUtils.defaultIfEmpty(sfx.getAffix(), "0")
            + " " + sfx.getCondition().getPatternText());
      } else if (rule instanceof AffixRule.PrefixRule pfx) {
        System.out.println(" "
            + StringUtils.defaultIfEmpty(pfx.getStrip(), "0")
            + " " + StringUtils.defaultIfEmpty(pfx.getAffix(), "0")
            + " " + pfx.getCondition().getPatternText());
      } else {
        throw new IllegalStateException("Unknown rule class: " + rule.getClass().getSimpleName());
      }
      if (++cnt > 5) {
        int rest = rules.size() - cnt;
        if (rest > 0) {
          System.out.println(" ... " + rest + " more");
        }
        break;
      }
    }
    System.out.println();
  }
}
