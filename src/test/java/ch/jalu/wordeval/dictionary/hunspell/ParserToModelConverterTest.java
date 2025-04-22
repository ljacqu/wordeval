package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.dictionary.hunspell.condition.AffixCondition;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixClass;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link ParserToModelConverter}.
 */
@ExtendWith(MockitoExtension.class)
class ParserToModelConverterTest {

  @InjectMocks
  private ParserToModelConverter converter;

  @Test
  void shouldConvertWithMinimalValues() {
    // given
    ParsedAffixClass parsedClass = new ParsedAffixClass();
    parsedClass.type = AffixType.SFX;
    parsedClass.flag = "N";
    parsedClass.crossProduct = true;

    ParsedAffixes parsedAffixes = new ParsedAffixes();
    parsedAffixes.addAffixClass(parsedClass);
    parsedAffixes.addRuleToCurrentClass(new ParsedAffixClass.Rule("s", "d", "es"));

    // when
    HunspellAffixes affixesDefinition = converter.convert(parsedAffixes);

    // then
    assertThat(affixesDefinition.getFlagType(), equalTo(AffixFlagType.SINGLE));
    assertThat(affixesDefinition.getNeedAffixFlag(), nullValue());
    assertThat(affixesDefinition.getAffixClassesByFlag().keySet(), contains("N"));
    AffixClass affixClass = affixesDefinition.getAffixClassesByFlag().get("N");
    assertThat(affixClass.getType(), equalTo(AffixType.SFX));
    assertThat(affixClass.getFlag(), equalTo("N"));
    assertThat(affixClass.isCrossProduct(), equalTo(true));

    assertThat(affixClass.getRules(), hasSize(1));
    AffixClass.SuffixRule rule = (AffixClass.SuffixRule) affixClass.getRules().getFirst();
    assertThat(rule.getStrip(), equalTo("s"));
    assertThat(rule.getSuffix(), equalTo("d"));
    assertThat(rule.getCondition().matches("benches"), equalTo(true));
    assertThat(rule.applyRule("benches"), equalTo("benched"));
  }

  @Test
  void shouldConvertDefinitions() {
    // given
    ParsedAffixClass parsedClass = new ParsedAffixClass();
    parsedClass.type = AffixType.PFX;
    parsedClass.flag = "P2";
    parsedClass.crossProduct = false;

    ParsedAffixes parsedAffixes = new ParsedAffixes();
    parsedAffixes.setFlagType(AffixFlagType.LONG);
    parsedAffixes.setNeedAffixFlag("{}");
    parsedAffixes.addAffixClass(parsedClass);
    parsedAffixes.addRuleToCurrentClass(new ParsedAffixClass.Rule("b", "m", "ba"));
    parsedAffixes.addRuleToCurrentClass(new ParsedAffixClass.Rule("p", "n", "p[^a]"));

    // when
    HunspellAffixes affixesDefinition = converter.convert(parsedAffixes);

    // then
    assertThat(affixesDefinition.getFlagType(), equalTo(AffixFlagType.LONG));
    assertThat(affixesDefinition.getNeedAffixFlag(), equalTo("{}"));
    assertThat(affixesDefinition.getAffixClassesByFlag().keySet(), contains("P2"));
    AffixClass affixClass = affixesDefinition.getAffixClassesByFlag().get("P2");
    assertThat(affixClass.getType(), equalTo(AffixType.PFX));
    assertThat(affixClass.isCrossProduct(), equalTo(false));

    assertThat(affixClass.getRules(), hasSize(2));
    // First rule: b m ba
    AffixClass.PrefixRule rule1 = (AffixClass.PrefixRule) affixClass.getRules().get(0);
    assertThat(rule1.getStrip(), equalTo("b"));
    assertThat(rule1.getPrefix(), equalTo("m"));
    assertThat(rule1.getCondition().matches("bake"), equalTo(true));
    assertThat(rule1.applyRule("bake"), equalTo("make"));
    // Second rule: p n p[^a]
    AffixClass.PrefixRule rule2 = (AffixClass.PrefixRule) affixClass.getRules().get(1);
    assertThat(rule2.getStrip(), equalTo("p"));
    assertThat(rule2.getPrefix(), equalTo("n"));
    assertThat(rule2.getCondition().matches("pun"), equalTo(true));
    assertThat(rule2.applyRule("pun"), equalTo("nun"));
  }

  @ParameterizedTest(name = "{1}: {0}")
  @MethodSource("getPatternConversionCases")
  void shouldConvertPattern_pfx_singleChar(String pattern, AffixType affixType, String expectedPatternClassName,
                                           String expectedPassingValue, String expectedFailingValue) {
    // given / when
    AffixCondition result = converter.convertCondition(pattern, affixType);

    // then
    assertThat(result.matches(expectedPassingValue), equalTo(true));
    if (expectedFailingValue != null) {
      assertThat(result.matches(expectedFailingValue), equalTo(false));
    }
    assertThat(result.getClass().getSimpleName(), equalTo(expectedPatternClassName));
  }

  static List<Arguments> getPatternConversionCases() {
    // Pattern, affix type, expected pattern type, matching example, non-matching example
    return List.of(
        Arguments.of("e", AffixType.PFX, "StartsWithSingleChar", "eat", "fat"),
        Arguments.of("t", AffixType.SFX, "EndsWithSingleChar", "cat", "cam"),
        Arguments.of("in", AffixType.PFX, "StartsWithSequence", "inapt", "enable"),
        Arguments.of("ng", AffixType.SFX, "EndsWithSequence", "sing", "sink"),
        Arguments.of("[ae]", AffixType.PFX, "StartsWith", "apt", "bat"),
        Arguments.of("[^ae]", AffixType.PFX, "StartsWith", "bat", "eat"),
        Arguments.of("[mn]", AffixType.SFX, "EndsWith", "can", "cap"),
        Arguments.of("[^mn]", AffixType.SFX, "EndsWith", "cap", "cam"),
        Arguments.of(".", AffixType.PFX, "AnyTokenCondition", "cap", null),
        Arguments.of(".", AffixType.SFX, "AnyTokenCondition", "cap", null),
        Arguments.of("a(m|n(d|t))", AffixType.PFX, "RegexCondition", "ant", "are"),
        Arguments.of("(d|t|v)en", AffixType.SFX, "RegexCondition", "given", "ramen")
    );
  }
}
