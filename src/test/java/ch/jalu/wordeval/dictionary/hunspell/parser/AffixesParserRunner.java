package ch.jalu.wordeval.dictionary.hunspell.parser;

import ch.jalu.wordeval.config.SpringContainedRunner;
import ch.jalu.wordeval.dictionary.hunspell.AffixClass;
import ch.jalu.wordeval.dictionary.hunspell.HunspellAffixes;
import ch.jalu.wordeval.dictionary.hunspell.ParserToModelConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

      System.out.println("Processed " + affixes.getAffixClassesByFlag().size() + " affix classes");
      affixes.getAffixClassesByFlag().values().forEach(this::printAffixClass);
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

  private void printAffixClass(AffixClass affixClass) {
    System.out.println(affixClass.getType() + " " + affixClass.getFlag());
    int cnt = 0;
    for (AffixClass.AffixRule rule : affixClass.getRules()) {
      if (rule instanceof AffixClass.SuffixRule sfx) {
        System.out.println(" "
            + StringUtils.defaultIfEmpty(sfx.getStrip(), "0")
            + " " + StringUtils.defaultIfEmpty(sfx.getSuffix(), "0")
            + " " + sfx.getCondition().getPatternText());
      } else if (rule instanceof AffixClass.PrefixRule pfx) {
        System.out.println(" "
            + StringUtils.defaultIfEmpty(pfx.getStrip(), "0")
            + " " + StringUtils.defaultIfEmpty(pfx.getPrefix(), "0")
            + " " + pfx.getCondition().getPatternText());
      } else {
        throw new IllegalStateException("Unknown rule class: " + rule.getClass().getSimpleName());
      }
      if (++cnt > 5) {
        int rest = affixClass.getRules().size() - cnt;
        if (rest > 0) {
          System.out.println(" ... " + rest + " more");
        }
        break;
      }
    }
    System.out.println();
  }
}
