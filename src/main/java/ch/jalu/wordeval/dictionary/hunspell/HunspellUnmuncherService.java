package ch.jalu.wordeval.dictionary.hunspell;

import com.google.common.base.Preconditions;
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

    populateWithAffixes(baseWord, affixFlags, results, affixDefinition);
    return results.stream();
  }

  // TODO: crossproduct flag is not considered.


  private void populateWithAffixes(String baseWord, List<String> affixFlags, List<String> results,
                                   HunspellAffixes affixDefinition) {
    affixFlags.stream()
        .flatMap(affixFlag -> affixDefinition.streamThroughMatchingRules(baseWord, affixFlag))
        .forEach(affixRule -> {
          String wordWithAffix = affixRule.applyRule(baseWord);
          results.add(wordWithAffix);


          if (!affixRule.getContinuationClasses().isEmpty()) {
            populateWithAffixes(wordWithAffix, affixRule.getContinuationClasses(), results, affixDefinition);
          }

          if (affixRule.getType() == AffixType.PFX) {
            affixFlags.stream()
                .flatMap(affixFlag -> affixDefinition.streamThroughMatchingRules(wordWithAffix, affixFlag))
                .filter(rule -> rule.getType() == AffixType.SFX)
                .forEach(suffixRule -> {
                  String suffixedResult = suffixRule.applyRule(wordWithAffix);
                  Preconditions.checkArgument(suffixRule.getContinuationClasses().isEmpty(),
                      "Unexpected continuation classes on suffix rule");
                  results.add(suffixedResult);
                });

          }
        });
  }
}
