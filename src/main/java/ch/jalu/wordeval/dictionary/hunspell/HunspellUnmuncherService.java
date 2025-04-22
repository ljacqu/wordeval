package ch.jalu.wordeval.dictionary.hunspell;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    String baseWord = line.substring(0, indexOfSlash);
    String affixFlagList = StringUtils.substringBefore(line.substring(indexOfSlash + 1), " ");
    List<String> affixFlags = affixDefinition.getFlagType().split(affixFlagList);

    List<String> results = new ArrayList<>();
    boolean includeBaseWord = affixDefinition.getNeedAffixFlag() == null
        || !affixFlags.contains(affixDefinition.getNeedAffixFlag());
    if (includeBaseWord) {
      results.add(baseWord);
    }

    for (String affixFlag : affixFlags) {
      applyAffixRule(baseWord, affixFlag, results, affixDefinition);
    }
    return results.stream();
  }

  // TODO: crossproduct flag is not considered.

  private void applyAffixRule(String baseWord, String affixFlag,
                              List<String> results, HunspellAffixes affixDefinition) {
    List<AffixClass.AffixRule> rules = affixDefinition.streamThroughMatchingRules(baseWord, affixFlag).toList();
    for (AffixClass.AffixRule rule : rules) {
      String newWord = rule.applyRule(baseWord);
      results.add(newWord);
      for (String continuationClass : rule.getContinuationClasses()) {
        applyAffixRule(newWord, continuationClass, results, affixDefinition);
      }
    }
  }

  // TODO: clean up
  // ChatGPT's opinion is to call this with (…, true, true) in the beginning, but this produces even fewer forms
  // than what the documentation suggests...
  private void applyAffixRule(String baseWord, String affixFlag,
                              List<String> results, HunspellAffixes affixDefinition,
                              boolean allowPrefixes, boolean allowSuffixes) {
    List<AffixClass.AffixRule> rules = affixDefinition.streamThroughMatchingRules(baseWord, affixFlag).toList();

    for (AffixClass.AffixRule rule : rules) {
      boolean isPrefix = rule.getType() == AffixType.PFX;
      boolean isSuffix = rule.getType() == AffixType.SFX;

      // Only apply if allowed in this stage
      if ((isPrefix && !allowPrefixes) || (isSuffix && !allowSuffixes)) {
        continue;
      }

      String newWord = rule.applyRule(baseWord);
      results.add(newWord);

      // Prefix before suffix: pass allowPrefixes = false when doing suffix pass
      for (String continuationClass : rule.getContinuationClasses()) {
        applyAffixRule(newWord, continuationClass, results, affixDefinition,
            // Once we've applied a prefix, only allow suffixes afterward
            isPrefix ? false : allowPrefixes,
            isSuffix ? false : allowSuffixes
        );
      }
    }
  }
}
