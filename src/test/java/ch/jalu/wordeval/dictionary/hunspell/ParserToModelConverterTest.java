package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.dictionary.hunspell.condition.AffixCondition;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixClass;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixes;
import com.google.common.collect.Iterables;
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
import static org.hamcrest.Matchers.empty;
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
    assertThat(affixesDefinition.getAffixRulesByFlag().keySet(), contains("N"));

    AffixRule affixRule = Iterables.getOnlyElement(affixesDefinition.getAffixRulesByFlag().get("N"));
    assertThat(affixRule.getType(), equalTo(AffixType.SFX));
    assertThat(affixRule.getStrip(), equalTo("s"));
    assertThat(affixRule.getAffix(), equalTo("d"));
    assertThat(affixRule.isCrossProduct(), equalTo(true));
    assertThat(affixRule.getCondition().matches("benches"), equalTo(true));
    assertThat(affixRule.applyRule("benches"), equalTo("benched"));
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
    assertThat(affixesDefinition.getAffixRulesByFlag().keySet(), contains("P2"));

    List<AffixRule> p2Rules = affixesDefinition.getAffixRulesByFlag().get("P2");
    p2Rules.forEach(rule -> {
      assertThat(rule.getType(), equalTo(AffixType.PFX));
      assertThat(rule.getContinuationClasses(), empty());
      assertThat(rule.isCrossProduct(), equalTo(false));
    });

    assertThat(p2Rules, hasSize(2));
    // First rule: b m ba
    AffixRule.PrefixRule rule1 = (AffixRule.PrefixRule) p2Rules.get(0);
    assertThat(rule1.getStrip(), equalTo("b"));
    assertThat(rule1.getAffix(), equalTo("m"));
    assertThat(rule1.getCondition().matches("bake"), equalTo(true));
    assertThat(rule1.applyRule("bake"), equalTo("make"));
    // Second rule: p n p[^a]
    AffixRule.PrefixRule rule2 = (AffixRule.PrefixRule) p2Rules.get(1);
    assertThat(rule2.getStrip(), equalTo("p"));
    assertThat(rule2.getAffix(), equalTo("n"));
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
