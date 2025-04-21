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
   * @param affixes the dictionary's affix rules
   * @return all words isolated and expanded
   */
  public Stream<String> unmunch(Stream<String> lines, HunspellAffixes affixes) {
    return lines.flatMap(line -> unmunch(line, affixes));
  }

  private Stream<String> unmunch(String line, HunspellAffixes affixDefinition) {
    int indexOfSlash = line.indexOf('/');
    if (indexOfSlash < 0) {
      return Stream.of(line);
    }

    String base = line.substring(0, indexOfSlash);
    String affixList = StringUtils.substringBefore(line.substring(indexOfSlash + 1), " ");
    List<String> affixes = affixDefinition.getFlagType().split(affixList);

    return applySuitableAffixes(base, affixes, affixDefinition);
  }

  private Stream<String> applySuitableAffixes(String baseWord, List<String> affixes, HunspellAffixes affixDefinition) {
    Stream<String> expandedWords = affixes.stream()
        .flatMap(affixFlag -> affixDefinition.streamThroughMatchingEntries(baseWord, affixFlag))
        .map(afxRule -> afxRule.applyRule(baseWord));

    return Stream.concat(Stream.of(baseWord), expandedWords);
  }
}
