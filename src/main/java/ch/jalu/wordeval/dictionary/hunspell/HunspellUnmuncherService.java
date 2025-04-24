package ch.jalu.wordeval.dictionary.hunspell;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Service to "unmunch" words (i.e. expand) based on Hunspell affix rules.
 */
@Service
public class HunspellUnmuncherService {

  /**
   * Processes the given dictionary lines and expands them based on the specified affix rules.
   *
   * @param lines the lines of the dictionary file (including affix information)
   * @param affixDefinition the dictionary's affix definitions
   * @return all words isolated and expanded
   */
  public Stream<String> unmunch(Stream<String> lines, HunspellAffixes affixDefinition) {
    return lines.flatMap(line -> unmunch(line, affixDefinition));
  }

  private Stream<String> unmunch(String line, HunspellAffixes affixDefinition) {
    int indexOfSlash = line.indexOf('/');
    if (indexOfSlash <= 0) {
      // We don't support empty strings as base
      return Stream.of(line);
    }
    // Slashes can be escaped with a backslash apparently (nl.dic), but this currently goes beyond the desired scope,
    // since a word with a slash won't be interesting to wordeval anyway. So it's the job of a sanitizer to skip these
    // words before they're passed to this class.
    Preconditions.checkArgument(line.indexOf('\\') < 0, line);

    String baseWord = line.substring(0, indexOfSlash);
    List<String> affixFlags = extractAffixFlags(line.substring(indexOfSlash + 1), affixDefinition.getFlagType());

    boolean includeBaseWord = affixDefinition.getNeedAffixFlag() == null
        || !affixFlags.contains(affixDefinition.getNeedAffixFlag());
    if (includeBaseWord) {
      return Stream.concat(Stream.of(baseWord), streamThroughAllAffixes(baseWord, affixFlags, affixDefinition));
    }
    return streamThroughAllAffixes(baseWord, affixFlags, affixDefinition);
  }

  /**
   * Returns the affix flags indicated in the "meta part" of the line, i.e. the section after the slash separating
   * the word.
   *
   * @param metaPart the part with the affixes
   * @param flagType flag type to parse the list with
   * @return all affix flags in the given meta part
   */
  private List<String> extractAffixFlags(String metaPart, AffixFlagType flagType) {
    int indexFirstWhitespace = -1;
    for (int i = 0; i < metaPart.length(); ++i) {
      if (Character.isWhitespace(metaPart.charAt(i))) {
        indexFirstWhitespace = i;
        break;
      }
    }

    String affixList = indexFirstWhitespace >= 0 ? metaPart.substring(0, indexFirstWhitespace) : metaPart;
    return flagType.split(affixList);
  }

  private Stream<String> streamThroughAllAffixes(String baseWord, List<String> affixFlags,
                                                 HunspellAffixes affixDefinition) {
    return affixFlags.stream()
        .flatMap(affixFlag -> affixDefinition.streamThroughMatchingRules(baseWord, affixFlag))
        .flatMap(affixRule -> {
          String wordWithAffix = affixRule.applyRule(baseWord);
          if (wordWithAffix == null) {
            return Stream.empty();
          }

          boolean hasContinuationClasses = !affixRule.getContinuationClasses().isEmpty();
          boolean isCrossProductPrefix = affixRule.getType() == AffixType.PFX && affixRule.isCrossProduct();
          if (hasContinuationClasses && isCrossProductPrefix) {
            return Stream.of(
                    Stream.of(wordWithAffix),
                    streamThroughAllAffixes(wordWithAffix, affixRule.getContinuationClasses(), affixDefinition),
                    addSuffixes(wordWithAffix, affixFlags, affixDefinition))
                .flatMap(Function.identity());
          } else if (hasContinuationClasses) {
            return Stream.concat(
                    Stream.of(wordWithAffix),
                    streamThroughAllAffixes(wordWithAffix, affixRule.getContinuationClasses(), affixDefinition));
          } else if (isCrossProductPrefix) {
            return Stream.concat(
                    Stream.of(wordWithAffix),
                    addSuffixes(wordWithAffix, affixFlags, affixDefinition));
          } else {
            return Stream.of(wordWithAffix);
          }
        });
  }

  private static Stream<String> addSuffixes(String word,
                                            List<String> affixFlags,
                                            HunspellAffixes affixDefinition) {
    return affixFlags.stream()
        .flatMap(affixFlag -> affixDefinition.getAffixRulesByFlag().get(affixFlag).stream())
        .filter(rule -> rule.getType() == AffixType.SFX && rule.isCrossProduct() && rule.matches(word))
        .map(rule -> rule.applyRule(word));
  }
}
