package ch.jalu.wordeval.dictionary.hunspell;

import ch.jalu.wordeval.dictionary.hunspell.condition.AffixCondition;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedAffixes;
import ch.jalu.wordeval.dictionary.hunspell.parser.ParsedRule;
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
import static org.hamcrest.Matchers.instanceOf;

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
    ParsedRule parsedRule = new ParsedRule();
    parsedRule.type = AffixType.SFX;
    parsedRule.flag = "N";
    parsedRule.crossProduct = true;

    ParsedAffixes parsedAffixes = new ParsedAffixes();
    parsedAffixes.addRule(parsedRule);
    parsedAffixes.addEntryToCurrentRule(new ParsedRule.RuleEntry("s", "t", "c"));

    // when
    HunspellAffixes affixesDefinition = converter.convert(parsedAffixes);

    // then
    assertThat(affixesDefinition.getFlagType(), equalTo(AffixFlagType.SINGLE));
    assertThat(affixesDefinition.getAffixRulesByName().keySet(), contains("N"));
    AffixRule affixRule = affixesDefinition.getAffixRulesByName().get("N");
    assertThat(affixRule.getType(), equalTo(AffixType.SFX));
    assertThat(affixRule.getFlag(), equalTo("N"));
    assertThat(affixRule.isCrossProduct(), equalTo(true));

    assertThat(affixRule.getRules(), hasSize(1));
    AffixRule.AffixRuleEntry rule = affixRule.getRules().getFirst();
    assertThat(rule, instanceOf(AffixRule.SuffixRuleEntry.class));
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
