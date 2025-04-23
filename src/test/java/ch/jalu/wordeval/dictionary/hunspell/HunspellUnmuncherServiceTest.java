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

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
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
    List<String> hWords = unmunchWord("h/NV po:let", affixesDef);

    // then
    assertThat(createWords, containsInAnyOrder("create", "procreate", "creative", "creation", "procreation", "procreative"));
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

  @Test
  void shouldNotReturnBaseWordIfHasNeedAffixFlag() {
    // given
    AffixClass affixClass = new AffixClass(AffixType.PFX, "14", false);
    affixClass.getRules().add(new AffixClass.PrefixRule("", "pro", emptyList(), AnyTokenCondition.INSTANCE));
    HunspellAffixes affixesDef = new HunspellAffixes();
    affixesDef.setFlagType(AffixFlagType.NUMBER);
    affixesDef.setNeedAffixFlag("53");
    affixesDef.setAffixClassesByFlag(Map.of("14", affixClass));

    // when
    List<String> result = unmunchWord("trude/14,53", affixesDef);

    // then
    assertThat(result, contains("protrude"));
  }

    /*
      PFX P Y 1
      PFX P   0 un . [prefix_un]+

      SFX S Y 1
      SFX S   0 s . +PL

      SFX Q Y 1
      SFX Q   0 s . +3SGV

      SFX R Y 1
      SFX R   0 able/PS . +DER_V_ADJ_ABLE
   */
  @Test
  void shouldFollowContinuationClasses() {
    // given
    AffixClass pfxP = new AffixClass(AffixType.PFX, "P", true);
    pfxP.getRules().add(new AffixClass.PrefixRule("", "un", emptyList(), AnyTokenCondition.INSTANCE));
    AffixClass sfxS = new AffixClass(AffixType.SFX, "S", true);
    sfxS.getRules().add(new AffixClass.SuffixRule("", "s", emptyList(), AnyTokenCondition.INSTANCE));
    AffixClass sfxR = new AffixClass(AffixType.SFX, "R", true);
    sfxR.getRules().add(new AffixClass.SuffixRule("", "able", List.of("P", "S"), AnyTokenCondition.INSTANCE));

    HunspellAffixes affixesDef = new HunspellAffixes();
    affixesDef.setFlagType(AffixFlagType.SINGLE);
    affixesDef.setAffixClassesByFlag(Map.of("P", pfxP, "S", sfxS, "R", sfxR));

    // when
    List<String> thinkWords = unmunchWord("drink/R", affixesDef);

    // then
    assertThat(thinkWords, containsInAnyOrder("drink", "drinkable", "drinkables", "undrinkable", "undrinkables"));
  }

  /*
    PFX A Y 1
    PFX A 0 re .

    SFX B Y 1
    SFX B 0 ed .

    SFX C Y 1
    SFX C 0 ing .
   */
  @Test
  void shouldApplyAffixesInCombination() {
    // given
    AffixClass pfxA = new AffixClass(AffixType.PFX, "A", true);
    pfxA.getRules().add(new AffixClass.PrefixRule("", "re", emptyList(), AnyTokenCondition.INSTANCE));
    AffixClass sfxB = new AffixClass(AffixType.SFX, "B", true);
    sfxB.getRules().add(new AffixClass.SuffixRule("", "ed", emptyList(), AnyTokenCondition.INSTANCE));
    AffixClass sfxC = new AffixClass(AffixType.SFX, "C", true);
    sfxC.getRules().add(new AffixClass.SuffixRule("", "ing", emptyList(), AnyTokenCondition.INSTANCE));

    HunspellAffixes affixesDef = new HunspellAffixes();
    affixesDef.setFlagType(AffixFlagType.SINGLE);
    affixesDef.setAffixClassesByFlag(Map.of("A", pfxA, "B", sfxB, "C", sfxC));

    // when
    List<String> words = unmunchWord("play/ABC", affixesDef);

    // then
    assertThat(words, containsInAnyOrder("play", "replay", "played", "playing", "replayed", "replaying"));
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
    AffixClass k = new AffixClass(AffixType.PFX, "K", true);
    k.getRules().add(new AffixClass.PrefixRule("", "pro", emptyList(), AnyTokenCondition.INSTANCE));

    AffixClass v = new AffixClass(AffixType.SFX, "V", false);
    v.getRules().add(new AffixClass.SuffixRule("e", "ive", emptyList(), newSuffixCondition("e")));
    v.getRules().add(new AffixClass.SuffixRule("", "ive", emptyList(), newSuffixCondition("[^e]")));

    AffixClass n = new AffixClass(AffixType.SFX, "N", false);
    n.getRules().add(new AffixClass.SuffixRule("e", "ion", emptyList(), newSuffixCondition("e")));
    n.getRules().add(new AffixClass.SuffixRule("y", "ication", emptyList(), newSuffixCondition("y")));
    n.getRules().add(new AffixClass.SuffixRule("", "en", emptyList(), newSuffixCondition("[^ey]")));

    HunspellAffixes affixes = new HunspellAffixes();
    affixes.setFlagType(AffixFlagType.SINGLE);
    affixes.setAffixClassesByFlag(Map.of("K", k, "V", v, "N", n));
    return affixes;
  }

  private static AffixCondition newPrefixCondition(String pattern) {
    return new RegexCondition(pattern, AffixType.PFX);
  }

  private static AffixCondition newSuffixCondition(String pattern) {
    return new RegexCondition(pattern, AffixType.SFX);
  }
}
