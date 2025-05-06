package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.dictionary.hunspell.sanitizer.RootAndAffixes;
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
   * Processes the given word and expands it based on the specified affix rules.
   *
   * @param rootAndAffixes the word and affix flags to apply
   * @param affixDefinition the dictionary's affix definitions
   * @return all words isolated and expanded
   */
  public Stream<String> unmunch(RootAndAffixes rootAndAffixes, HunspellAffixes affixDefinition) {
    String root = rootAndAffixes.root();
    List<String> affixFlags = affixDefinition.getFlagType().split(rootAndAffixes.affixFlags());

    // Slashes can be escaped with a backslash apparently (nl.dic), but this currently goes beyond the desired scope,
    // since a word with a slash won't be interesting to wordeval anyway. So it's the job of a sanitizer to skip these
    // words before they're passed to this class.
    Preconditions.checkArgument(root.indexOf('\\') < 0, rootAndAffixes);

    if (affixDefinition.getForbiddenWordClass() != null
        && affixFlags.contains(affixDefinition.getForbiddenWordClass())) {
      return Stream.empty();
    }

    boolean includeRoot = affixDefinition.getNeedAffixFlag() == null
        || !affixFlags.contains(affixDefinition.getNeedAffixFlag());
    if (includeRoot) {
      return Stream.concat(Stream.of(root), streamThroughAllAffixes(root, affixFlags, affixDefinition));
    }
    return streamThroughAllAffixes(root, affixFlags, affixDefinition);
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
