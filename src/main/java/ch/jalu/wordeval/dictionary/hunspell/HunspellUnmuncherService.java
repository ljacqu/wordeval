package ch.jalu.wordeval.dictionary.hunspell;

import com.google.common.base.Preconditions;
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
    // Slashes can be escaped with a backslash apparently (nl.dic), but this currently goes beyond the desired scope,
    // since a word with a slash won't be interesting to wordeval anyway. So it's the job of a sanitizer to skip these
    // words before they're passed to this class.
    Preconditions.checkArgument(line.indexOf('\\') < 0, line);

    String baseWord = line.substring(0, indexOfSlash);
    List<String> affixFlags = returnAffixFlags(line.substring(indexOfSlash + 1), affixDefinition.getFlagType());

    List<String> results = new ArrayList<>();
    boolean includeBaseWord = affixDefinition.getNeedAffixFlag() == null
        || !affixFlags.contains(affixDefinition.getNeedAffixFlag());
    if (includeBaseWord) {
      results.add(baseWord);
    }

    populateWithAffixes(baseWord, affixFlags, results, affixDefinition);
    return results.stream();
  }

  /**
   * Returns the affix flags indicated in the "meta part" of the line, i.e. the section after the slash separating
   * the word.
   *
   * @param metaPart the part with the affixes
   * @param flagType flag type to parse the list with
   * @return all affix flags in the given meta part
   */
  private List<String> returnAffixFlags(String metaPart, AffixFlagType flagType) {
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

  private void populateWithAffixes(String baseWord, List<String> affixFlags, List<String> results,
                                   HunspellAffixes affixDefinition) {
    affixFlags.stream()
        .flatMap(affixFlag -> affixDefinition.streamThroughMatchingRules(baseWord, affixFlag))
        .forEach(affixRule -> {
          String wordWithAffix = affixRule.applyRule(baseWord);
          if (wordWithAffix == null) {
            return;
          }
          results.add(wordWithAffix);


          if (!affixRule.getContinuationClasses().isEmpty()) {
            populateWithAffixes(wordWithAffix, affixRule.getContinuationClasses(), results, affixDefinition);
          }

          if (affixRule.getType() == AffixType.PFX && affixRule.isCrossProduct()) {
            affixFlags.stream()
                .flatMap(affixFlag -> affixDefinition.streamThroughMatchingRules(wordWithAffix, affixFlag))
                .filter(rule -> rule.getType() == AffixType.SFX && rule.isCrossProduct())
                .forEach(suffixRule -> {
                  String suffixedResult = suffixRule.applyRule(wordWithAffix);
                  if (suffixedResult == null) {
                    return;
                  }
                  Preconditions.checkArgument(suffixRule.getContinuationClasses().isEmpty(),
                      "Unexpected continuation classes on suffix rule");
                  results.add(suffixedResult);
                });

          }
        });
  }
}
