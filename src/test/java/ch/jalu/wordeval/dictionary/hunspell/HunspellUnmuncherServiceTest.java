package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.dictionary.hunspell.condition.AffixCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.AnyTokenCondition;
import ch.jalu.wordeval.dictionary.hunspell.condition.RegexCondition;
import ch.jalu.wordeval.dictionary.hunspell.sanitizer.HunspellSanitizer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

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
    List<String> createWords = unmunchWord(affixesDef, "create/KNV");
    List<String> ablateWords = unmunchWord(affixesDef, "ablate/NV");
    List<String> hWords = unmunchWord(affixesDef, "h/NV po:let");

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
    List<String> applesWords = unmunchWord(affixesDef, "apple");
    List<String> pastaWords = unmunchWord(affixesDef, "pasta/AB");

    // then
    assertThat(applesWords, containsInAnyOrder("apple"));
    assertThat(pastaWords, containsInAnyOrder("pasta"));
  }

  @Test
  void shouldParseAffixesUntilWhitespace() {
    // given
    HunspellAffixes affixes = createSampleEnglishDefinitions();
    affixes.setFlagType(AffixFlagType.SINGLE);

    // when
    List<String> result1 = unmunchWord(affixes, "duck/K VN");
    List<String> result2 = unmunchWord(affixes, "duck/K\tVN test");

    // then
    assertThat(result1, containsInAnyOrder("duck", "produck"));
    assertThat(result2, containsInAnyOrder("duck", "produck"));
  }

  @Test
  void shouldNotReturnBaseWordIfHasNeedAffixFlag() {
    // given
    ListMultimap<String, AffixRule> rulesByFlag = ArrayListMultimap.create();
    rulesByFlag.put("14", newPrefixRule("", "pro", "."));
    HunspellAffixes affixesDef = new HunspellAffixes();
    affixesDef.setFlagType(AffixFlagType.NUMBER);
    affixesDef.setNeedAffixFlag("53");
    affixesDef.setAffixRulesByFlag(rulesByFlag);

    // when
    List<String> result = unmunchWord(affixesDef, "trude/14,53");

    // then
    assertThat(result, contains("protrude"));
  }

  /*
     Modified from nl.aff:
       SFX Zf ak ken k
       SFX Zf ek ken k
   */
  @Test
  void shouldOnlyApplyRulesWhenStripCanBeApplied() {
    // given
    ListMultimap<String, AffixRule> rulesByFlag = ArrayListMultimap.create();
    rulesByFlag.put("Zf", newSuffixRule("ak", "ken", "k"));
    rulesByFlag.put("Zf", newSuffixRule("ek", "ken", "k"));

    HunspellAffixes affixesDef = new HunspellAffixes();
    affixesDef.setFlagType(AffixFlagType.LONG);
    affixesDef.setAffixRulesByFlag(rulesByFlag);

    // when
    List<String> words = unmunchWord(affixesDef, "Azteek/Zf");

    // then
    assertThat(words, containsInAnyOrder("Azteek", "Azteken"));
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
    ListMultimap<String, AffixRule> rulesByFlag = ArrayListMultimap.create();
    rulesByFlag.put("P", newPrefixRule("", "un", "."));
    rulesByFlag.put("S", newSuffixRule("", "s", "."));
    rulesByFlag.put("R", new AffixRule.SuffixRule("", "able", List.of("P", "S"), AnyTokenCondition.INSTANCE, true));

    HunspellAffixes affixesDef = new HunspellAffixes();
    affixesDef.setFlagType(AffixFlagType.SINGLE);
    affixesDef.setAffixRulesByFlag(rulesByFlag);

    // when
    List<String> thinkWords = unmunchWord(affixesDef, "drink/R");

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
    ListMultimap<String, AffixRule> rulesByFlag = ArrayListMultimap.create();
    rulesByFlag.put("A", newPrefixRule("", "re", "."));
    rulesByFlag.put("B", newSuffixRule("", "ed", "."));
    rulesByFlag.put("C", newSuffixRule("", "ing", "."));

    HunspellAffixes affixesDef = new HunspellAffixes();
    affixesDef.setFlagType(AffixFlagType.SINGLE);
    affixesDef.setAffixRulesByFlag(rulesByFlag);

    // when
    List<String> words = unmunchWord(affixesDef, "play/ABC");

    // then
    assertThat(words, containsInAnyOrder("play", "replay", "played", "playing", "replayed", "replaying"));
  }

  /*
    SFX E Y 211
    SFX E 0 mi [ts]o
    SFX E 0 ti [ts]o

    SFX y Y 4
    SFX y o issimo o
    SFX y o issima o

    SFX Y N 16
    SFX Y o emente [lu]ento
    SFX Y o amente [^t]o
    SFX Y o amente [^n]to
    SFX Y o issimamente [^i]o

    PFX J Y 1
    PFX J 0 ri .
   */
  @Test
  void shouldNotCombineNonCrossProductAffixes() {
    // given
    ListMultimap<String, AffixRule> rulesByFlag = ArrayListMultimap.create();
    rulesByFlag.put("E", newSuffixRule("", "mi", "[ts]o"));
    rulesByFlag.put("E", newSuffixRule("", "ti", "[ts]o"));
    rulesByFlag.put("y", newSuffixRule("o", "issimo", "o"));
    rulesByFlag.put("y", newSuffixRule("o", "issima", "o"));
    rulesByFlag.put("Y", new AffixRule.SuffixRule("o", "emente", emptyList(), newSuffixCondition("[lu]ento"), false)); // does not apply
    rulesByFlag.put("Y", new AffixRule.SuffixRule("o", "amente", emptyList(), newSuffixCondition("[^t]o"), false)); // does not apply
    rulesByFlag.put("Y", new AffixRule.SuffixRule("o", "amente", emptyList(), newSuffixCondition("[^n]to"), false));
    rulesByFlag.put("Y", new AffixRule.SuffixRule("o", "issimamente", emptyList(), newSuffixCondition("[^i]o"), false));
    rulesByFlag.put("J", newPrefixRule("", "ri", "."));

    HunspellAffixes affixesDef = new HunspellAffixes();
    affixesDef.setFlagType(AffixFlagType.SINGLE);
    affixesDef.setAffixRulesByFlag(rulesByFlag);

    // when
    List<String> words = unmunchWord(affixesDef, "perduto/EyYJ");

    // then
    // SFX Y not applied with PFX J, i.e. no *riperdutamente or *riperdutissimamente
    assertThat(words, containsInAnyOrder("perduto", "perdutomi", "perdutoti", "perdutissimo", "perdutissima", "perdutamente", "perdutissimamente",
        "riperduto", "riperdutomi", "riperdutoti", "riperdutissimo", "riperdutissima"));
  }

  @Test
  void shouldHandleWordWithSpace() {
    // given
    HunspellAffixes affixDefinition = createSampleEnglishDefinitions();

    // when
    List<String> words1 = unmunchWord(affixDefinition, "Puerto Rico");
    List<String> words2 = unmunchWord(affixDefinition, "Puerto Rico/K");
    List<String> words3 = unmunchWord(affixDefinition, "Puerto Rico/K Now some other text");

    // then
    assertThat(words1, contains("Puerto Rico"));
    assertThat(words2, containsInAnyOrder("Puerto Rico", "proPuerto Rico"));
    assertThat(words3, containsInAnyOrder("Puerto Rico", "proPuerto Rico"));
  }

  @Test
  void shouldSkipWordsWithForbiddenWordClass() {
    // given
    HunspellAffixes affixDefinition = createSampleEnglishDefinitions();
    affixDefinition.setForbiddenWordClass("W");

    // when
    List<String> result = unmunchWord(affixDefinition, "suport/W", "support/V");

    // then
    assertThat(result, containsInAnyOrder("support", "supportive"));
  }

  @Test
  void shouldHandlePrefixAndSuffixWithSameName() {
    // given
    HunspellAffixes affixDefinition = new HunspellAffixes();
    affixDefinition.setFlagType(AffixFlagType.SINGLE);
    affixDefinition.setAffixRulesByFlag(ArrayListMultimap.create());
    affixDefinition.getAffixRulesByFlag().put("A", newPrefixRule("", "re", "."));
    affixDefinition.getAffixRulesByFlag().put("A", newSuffixRule("", "ed", "[^e]"));
    affixDefinition.getAffixRulesByFlag().put("A", newSuffixRule("", "d", "e"));

    // when
    List<String> words = unmunchWord(affixDefinition, "start/A");

    // then
    assertThat(words, containsInAnyOrder("start", "started", "restart", "restarted"));
  }

  private List<String> unmunchWord(HunspellAffixes affixesDefinition, String... words) {
    HunspellSanitizer sanitizer = new HunspellSanitizer();
    return Arrays.stream(words)
        .map(sanitizer::split)
        .flatMap(baseWordAndAffixes -> unmuncherService.unmunch(baseWordAndAffixes, affixesDefinition))
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
    ListMultimap<String, AffixRule> rulesByFlag = ArrayListMultimap.create();
    rulesByFlag.put("K", newPrefixRule("", "pro", "."));
    rulesByFlag.put("V", newSuffixRule("e", "ive", "e"));
    rulesByFlag.put("V", newSuffixRule("", "ive", "[^e]"));
    rulesByFlag.put("N", newSuffixRule("e", "ion", "e"));
    rulesByFlag.put("N", newSuffixRule("y", "ication", "y"));
    rulesByFlag.put("N", newSuffixRule("", "en", "[^ey]"));

    HunspellAffixes affixes = new HunspellAffixes();
    affixes.setFlagType(AffixFlagType.SINGLE);
    affixes.setAffixRulesByFlag(rulesByFlag);
    return affixes;
  }

  private static AffixCondition newPrefixCondition(String pattern) {
    return new RegexCondition(pattern, AffixType.PFX);
  }

  private static AffixCondition newSuffixCondition(String pattern) {
    return new RegexCondition(pattern, AffixType.SFX);
  }

  private static AffixRule.PrefixRule newPrefixRule(String strip, String prefix, String condition) {
    return new AffixRule.PrefixRule(strip, prefix, emptyList(), newPrefixCondition(condition), true);
  }

  private static AffixRule.SuffixRule newSuffixRule(String strip, String suffix, String condition) {
    return new AffixRule.SuffixRule(strip, suffix, emptyList(), newSuffixCondition(condition), true);
  }
}
