package ch.jalu.wordeval.dictionary.hunspell.parser;

import ch.jalu.wordeval.config.SpringContainedRunner;
import ch.jalu.wordeval.dictionary.hunspell.AffixRule;
import ch.jalu.wordeval.dictionary.hunspell.HunspellAffixes;
import ch.jalu.wordeval.dictionary.hunspell.ParserToModelConverter;
import com.google.common.collect.Multimaps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Runner that prints out the parsed affix rules of a dictionary file.
 */
public class AffixesParserRunner extends SpringContainedRunner {

  @Autowired
  private AffixesParser parser;

  @Autowired
  private ParserToModelConverter converter;

  public static void main(String[] args) {
    runApplication(AffixesParserRunner.class, args);
  }

  @Override
  public void run(String... args) {
    try (Scanner scanner = new Scanner(System.in)) {
      System.out.println("Language code: ");
      String code = scanner.nextLine().trim();

      Path affFile = Paths.get("dict/" + code + ".aff");
      HunspellAffixes affixes = loadAndParseAffixes(affFile);

      System.out.println("Processed " + affixes.getAffixRulesByFlag().keySet().size() + " affix classes");
      Multimaps.asMap(affixes.getAffixRulesByFlag())
          .forEach(this::printAffixClass);
    }
  }

  private HunspellAffixes loadAndParseAffixes(Path file) {
    ParsedAffixes parsedAffixes;
    try (Stream<String> lines = Files.lines(file)) {
      parsedAffixes = parser.parseAffFile(lines);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read '" + file + "'", e);
    }

    return converter.convert(parsedAffixes);
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
