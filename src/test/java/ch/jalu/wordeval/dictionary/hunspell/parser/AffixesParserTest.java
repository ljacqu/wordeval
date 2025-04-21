package ch.jalu.wordeval.dictionary.hunspell.parser;

import ch.jalu.wordeval.dictionary.hunspell.AffixFlagType;
import ch.jalu.wordeval.dictionary.hunspell.AffixType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test for {@link AffixesParser}.
 */
class AffixesParserTest {

  private final AffixesParser parser = new AffixesParser();

  /** Small excerpt from en-us.aff. */
  @Test
  void shouldParseSimpleFile() {
    // given
    List<String> lines = List.of(
        "PFX X Y 2",
        "PFX X   0     re       .",
        "PFX X   dis   un       dis",
        "",
        "# Sample suffix",
        "SFX N Y 3",
        "SFX N   e     ion        e",
        "SFX N   y     ication    y ",
        "SFX N   0     en         [^ey]");

    // when
    ParsedAffixes result = parser.parseAffFile(lines.stream());

    // then
    assertThat(result.getFlagType(), nullValue());
    assertThat(result.getRules(), hasSize(2));

    ParsedRule prefixRule = result.getRules().stream().filter(rule -> "X".equals(rule.flag)).findFirst().orElseThrow();
    assertThat(prefixRule.type, equalTo(AffixType.PFX));
    assertThat(prefixRule.crossProduct, equalTo(true));
    assertThat(prefixRule.rules, hasSize(2));
    assertThat(prefixRule.rules.get(0).strip, equalTo(""));
    assertThat(prefixRule.rules.get(0).append, equalTo("re"));
    assertThat(prefixRule.rules.get(0).condition, equalTo("."));
    assertThat(prefixRule.rules.get(1).strip, equalTo("dis"));
    assertThat(prefixRule.rules.get(1).append, equalTo("un"));
    assertThat(prefixRule.rules.get(1).condition, equalTo("dis"));

    ParsedRule suffixRule = result.getRules().stream().filter(rule -> "N".equals(rule.flag)).findFirst().orElseThrow();
    assertThat(suffixRule.type, equalTo(AffixType.SFX));
    assertThat(suffixRule.crossProduct, equalTo(true));
    assertThat(suffixRule.rules, hasSize(3));
    assertThat(suffixRule.rules.get(0).strip, equalTo("e"));
    assertThat(suffixRule.rules.get(0).append, equalTo("ion"));
    assertThat(suffixRule.rules.get(0).condition, equalTo("e"));
    assertThat(suffixRule.rules.get(1).strip, equalTo("y"));
    assertThat(suffixRule.rules.get(1).append, equalTo("ication"));
    assertThat(suffixRule.rules.get(1).condition, equalTo("y"));
    assertThat(suffixRule.rules.get(2).strip, equalTo(""));
    assertThat(suffixRule.rules.get(2).append, equalTo("en"));
    assertThat(suffixRule.rules.get(2).condition, equalTo("[^ey]"));
  }

  /** Excerpt from da.aff. */
  @Test
  void shouldParseRulesWithMorphologicalAdditions() {
    // given
    List<String> lines = List.of(
        "FLAG num",
        "",
        "SFX 9 N 120 ",
        "SFX 9 0 bet/944 b\t+KONSONANT_FORDOBLING",
        "SFX 9 0 ber/944 b\t+KONSONANT_FORDOBLING",
        "SFX 9 0 berne/944 b\t+KONSONANT_FORDOBLING");

    // when
    ParsedAffixes result = parser.parseAffFile(lines.stream());

    // then
    assertThat(result.getFlagType(), equalTo(AffixFlagType.NUMBER));
    assertThat(result.getRules(), hasSize(1));
    assertThat(result.getRules().getFirst().crossProduct, equalTo(false));
    assertThat(result.getRules().getFirst().rules, hasSize(3));
    assertThat(result.getRules().getFirst().rules.get(0).strip, equalTo(""));
    assertThat(result.getRules().getFirst().rules.get(0).append, equalTo("bet/944"));
    assertThat(result.getRules().getFirst().rules.get(0).condition, equalTo("b"));
  }
}