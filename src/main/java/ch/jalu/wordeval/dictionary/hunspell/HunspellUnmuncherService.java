package ch.jalu.wordeval.dictionary.hunspell;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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

    String base = line.substring(0, indexOfSlash);
    String affixFlagList = StringUtils.substringBefore(line.substring(indexOfSlash + 1), " ");
    List<String> affixFlags = affixDefinition.getFlagType().split(affixFlagList);

    return applySuitableAffixRules(base, affixFlags, affixDefinition);
  }

  private Stream<String> applySuitableAffixRules(String baseWord, List<String> affixes,
                                                 HunspellAffixes affixDefinition) {
    Stream<String> expandedWords = affixes.stream()
        .flatMap(affixFlag -> affixDefinition.streamThroughMatchingRules(baseWord, affixFlag))
        .map(afxRule -> afxRule.applyRule(baseWord));

    return Stream.concat(Stream.of(baseWord), expandedWords);
  }
}
