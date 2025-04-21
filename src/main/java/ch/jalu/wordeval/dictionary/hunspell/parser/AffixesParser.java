package ch.jalu.wordeval.dictionary.hunspell.parser;

import ch.jalu.wordeval.dictionary.hunspell.AffixFlagType;
import ch.jalu.wordeval.dictionary.hunspell.AffixType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Hunspell .aff parser.
 */
@Component
public class AffixesParser {

    // TODO: twofold suffix stripping is a thing  (e.g. `SFX X 0 able/Y .`)
    // https://www.systutorials.com/docs/linux/man/4-hunspell/

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
                ParsedAffixClass affixClass = new ParsedAffixClass();
                affixClass.type = AffixType.fromString(headerMatcher.group(1));
                affixClass.flag = headerMatcher.group(2);
                affixClass.crossProduct = headerMatcher.group(3).equalsIgnoreCase("Y");
                result.addAffixClass(affixClass);
            } else if (ruleMatcher.matches()) {
                String strip = ruleMatcher.group(2).equals("0") ? "" : ruleMatcher.group(2);
                String affix = ruleMatcher.group(3).equals("0") ? "" : ruleMatcher.group(3);
                String condition = ruleMatcher.group(4);
                result.addRuleToCurrentClass(new ParsedAffixClass.Rule(strip, affix, condition));
            } else if (StringUtils.startsWithAny(line,
                "REP ", "MAP ", "TRY ", "WORDCHARS ", "ICONV ", "OCONV ", "KEY ", "BREAK ")) {
                // ignore
            } else if (line.startsWith("FLAG ")) {
                result.setFlagType(AffixFlagType.fromAffixFileString(line.substring("FLAG ".length())));
            } else {
                // todo: logging
                System.out.println("Unknown line: " + line);
            }
        });

        return result;
    }
}
