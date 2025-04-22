package ch.jalu.wordeval.dictionary.hunspell.parser;

import ch.jalu.wordeval.dictionary.hunspell.AffixFlagType;
import ch.jalu.wordeval.dictionary.hunspell.AffixType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Hunspell .aff parser.
 */
@Slf4j
@Component
public class AffixesParser {

  // useful documentation: https://linux.die.net/man/4/hunspell

  // e.g. SFX V N 2
  private static final Pattern AFFIX_CLASS_HEADER_PATTERN =
      Pattern.compile("^(PFX|SFX)\\s+(\\S+)\\s+([YN])\\s+(\\d+)$");
  // e.g. SFX V   e  ive  e
  private static final Pattern AFFIX_RULE_PATTERN =
      Pattern.compile("^(PFX|SFX)\\s+\\S+\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)(.*?)?$");

  public ParsedAffixes parseAffFile(Stream<String> lines) {
    ParsedAffixes result = new ParsedAffixes();

    lines.forEach(line -> {
      line = line.trim();
      if (line.isEmpty() || line.startsWith("#")) {
        return;
      }

      Matcher headerMatcher = AFFIX_CLASS_HEADER_PATTERN.matcher(line);
      Matcher ruleMatcher = AFFIX_RULE_PATTERN.matcher(line);

      if (headerMatcher.matches()) {
        ParsedAffixClass affixClass = mapAffixClass(headerMatcher);
        result.addAffixClass(affixClass);
      } else if (ruleMatcher.matches()) {
        ParsedAffixClass.Rule rule = mapAffixRule(ruleMatcher, result.getFlagType());
        result.addRuleToCurrentClass(rule);
      } else if (line.startsWith("FLAG ")) {
        result.setFlagType(AffixFlagType.fromAffixFileString(line.substring("FLAG ".length())));
      } else if (line.startsWith("NEEDAFFIX ")) {
        result.setNeedAffixFlag(line.substring("NEEDAFFIX ".length()));
      } else {
        handleUnknownLine(line);
      }
    });

    return result;
  }

  private static ParsedAffixClass mapAffixClass(Matcher headerMatcher) {
    ParsedAffixClass affixClass = new ParsedAffixClass();
    affixClass.type = AffixType.fromString(headerMatcher.group(1));
    affixClass.flag = headerMatcher.group(2);
    affixClass.crossProduct = headerMatcher.group(3).equalsIgnoreCase("Y");
    return affixClass;
  }

  private static ParsedAffixClass.Rule mapAffixRule(Matcher ruleMatcher, AffixFlagType flagType) {
    String strip = ruleMatcher.group(2).equals("0") ? "" : ruleMatcher.group(2);
    String affix = ruleMatcher.group(3).equals("0") ? "" : ruleMatcher.group(3);
    String condition = ruleMatcher.group(4);

    int slashIndex = affix.indexOf('/');
    if (slashIndex < 0) {
      return new ParsedAffixClass.Rule(strip, affix, condition);
    }

    String continuationClasses = affix.substring(slashIndex + 1);
    affix = affix.substring(0, slashIndex);
    return new ParsedAffixClass.Rule(strip, affix, flagType.split(continuationClasses), condition);
  }

  private void handleUnknownLine(String line) {
    if (StringUtils.startsWithAny(line, "REP ", "MAP ", "TRY ", "WORDCHARS ", "ICONV ",
        "OCONV ", "KEY ", "BREAK ", "CHECKSHARPS", "NOSUGGEST ")) {
      // Nothing to do: command is not relevant for this application
      return;
    } else if (line.startsWith("SET ")) {
      if (!line.startsWith("SET UTF-8")) {
        log.warn("Found unexpected encoding directive: {}", line);
      }
    } else {
      log.info("Unknown line: {}", line);
    }
  }
}
