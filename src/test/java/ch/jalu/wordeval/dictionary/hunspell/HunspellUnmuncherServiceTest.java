package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.dictionary.hunspell.condition.AffixCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.AnyTokenCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.RegexCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Test for {@link HunspellUnmuncherService}.
 */
@ExtendWith(MockitoExtension.class)
class HunspellUnmuncherServiceTest {

  @InjectMocks
  private HunspellUnmuncherService unmuncherService;

  @Test
  void shouldUnmunch() {
    // given
    HunspellAffixes affixesDef = createSampleEnglishDefinitions();

    // when
    List<String> createWords = unmunchWord("create/KNV", affixesDef);
    List<String> ablateWords = unmunchWord("ablate/NV", affixesDef);
    List<String> hWords = unmunchWord("h/NV", affixesDef);

    // then
    assertThat(createWords, containsInAnyOrder("create", "procreate", "creative", "creation"));
    assertThat(ablateWords, containsInAnyOrder("ablate", "ablative", "ablation"));
    assertThat(hWords, containsInAnyOrder("h", "hive", "hen"));
  }

  @Test
  void shouldKeepBaseWordIfNoRulesApply() {
    // given
    HunspellAffixes affixesDef = createSampleEnglishDefinitions();

    // when
    List<String> applesWords = unmunchWord("apple", affixesDef);
    List<String> pastaWords = unmunchWord("pasta/AB", affixesDef);

    // then
    assertThat(applesWords, containsInAnyOrder("apple"));
    assertThat(pastaWords, containsInAnyOrder("pasta"));
  }

  private List<String> unmunchWord(String word, HunspellAffixes affixesDefinition) {
    return unmuncherService.unmunch(Stream.of(word), affixesDefinition)
        .toList();
  }

  /*
      PFX K Y 1
      PFX K   0     pro         .

      SFX V N 2
      SFX V   e     ive        e
      SFX V   0     ive        [^e]

      SFX N Y 3
      SFX N   e     ion        e
      SFX N   y     ication    y
      SFX N   0     en         [^ey]
   */
  private HunspellAffixes createSampleEnglishDefinitions() {
    AffixRule k = new AffixRule(AffixType.PFX, "K", true);
    k.getRules().add(new AffixRule.PrefixRuleEntry(AnyTokenCondition.INSTANCE, "", "pro"));

    AffixRule v = new AffixRule(AffixType.SFX, "V", false);
    v.getRules().add(new AffixRule.SuffixRuleEntry(newSuffixCondition("e"), "e", "ive"));
    v.getRules().add(new AffixRule.SuffixRuleEntry(newSuffixCondition("[^e]"), "", "ive"));

    AffixRule n = new AffixRule(AffixType.SFX, "N", false);
    n.getRules().add(new AffixRule.SuffixRuleEntry(newSuffixCondition("e"), "e", "ion"));
    n.getRules().add(new AffixRule.SuffixRuleEntry(newSuffixCondition("y"), "y", "ication"));
    n.getRules().add(new AffixRule.SuffixRuleEntry(newSuffixCondition("[^ey]"), "", "en"));

    HunspellAffixes affixes = new HunspellAffixes();
    affixes.setFlagType(AffixFlagType.SINGLE);
    affixes.setAffixRulesByName(Map.of("K", k, "V", v, "N", n));
    return affixes;
  }

  private static AffixCondition newPrefixCondition(String pattern) {
    return new RegexCondition(pattern, AffixType.PFX);
  }

  private static AffixCondition newSuffixCondition(String pattern) {
    return new RegexCondition(pattern, AffixType.SFX);
  }
}
