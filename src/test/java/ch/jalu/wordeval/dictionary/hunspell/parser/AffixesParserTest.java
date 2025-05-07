package ch.jalu.wordeval.dictionary.hunspell.parser;

import ch.jalu.wordeval.dictionary.hunspell.AffixFlagType;
import ch.jalu.wordeval.dictionary.hunspell.AffixType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyString;
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
    assertThat(result.getFlagType(), equalTo(AffixFlagType.SINGLE));
    assertThat(result.getNeedAffixFlag(), nullValue());
    assertThat(result.getForbiddenWordClass(), nullValue());
    assertThat(result.getOnlyInCompound(), nullValue());

    assertThat(result.getAffixClasses(), hasSize(2));

    ParsedAffixClass prefixClass = result.getAffixClasses().stream().filter(rule -> "X".equals(rule.flag)).findFirst().orElseThrow();
    assertThat(prefixClass.type, equalTo(AffixType.PFX));
    assertThat(prefixClass.crossProduct, equalTo(true));
    assertThat(prefixClass.rules, hasSize(2));
    assertThat(prefixClass.rules.get(0).strip(), equalTo(""));
    assertThat(prefixClass.rules.get(0).affix(), equalTo("re"));
    assertThat(prefixClass.rules.get(0).continuationClasses(), empty());
    assertThat(prefixClass.rules.get(0).condition(), equalTo("."));
    assertThat(prefixClass.rules.get(1).strip(), equalTo("dis"));
    assertThat(prefixClass.rules.get(1).affix(), equalTo("un"));
    assertThat(prefixClass.rules.get(1).continuationClasses(), empty());
    assertThat(prefixClass.rules.get(1).condition(), equalTo("dis"));

    ParsedAffixClass suffixClass = result.getAffixClasses().stream().filter(rule -> "N".equals(rule.flag)).findFirst().orElseThrow();
    assertThat(suffixClass.type, equalTo(AffixType.SFX));
    assertThat(suffixClass.crossProduct, equalTo(true));
    assertThat(suffixClass.rules, hasSize(3));
    assertThat(suffixClass.rules.get(0).strip(), equalTo("e"));
    assertThat(suffixClass.rules.get(0).affix(), equalTo("ion"));
    assertThat(suffixClass.rules.get(0).continuationClasses(), empty());
    assertThat(suffixClass.rules.get(0).condition(), equalTo("e"));
    assertThat(suffixClass.rules.get(1).strip(), equalTo("y"));
    assertThat(suffixClass.rules.get(1).affix(), equalTo("ication"));
    assertThat(suffixClass.rules.get(1).continuationClasses(), empty());
    assertThat(suffixClass.rules.get(1).condition(), equalTo("y"));
    assertThat(suffixClass.rules.get(2).strip(), equalTo(""));
    assertThat(suffixClass.rules.get(2).affix(), equalTo("en"));
    assertThat(suffixClass.rules.get(2).continuationClasses(), empty());
    assertThat(suffixClass.rules.get(2).condition(), equalTo("[^ey]"));
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
    assertThat(result.getAffixClasses(), hasSize(1));
    assertThat(result.getAffixClasses().getFirst().crossProduct, equalTo(false));
    assertThat(result.getAffixClasses().getFirst().rules, hasSize(3));
    assertThat(result.getAffixClasses().getFirst().rules.get(0).strip(), equalTo(""));
    assertThat(result.getAffixClasses().getFirst().rules.get(0).affix(), equalTo("bet"));
    assertThat(result.getAffixClasses().getFirst().rules.get(0).continuationClasses(), contains("944"));
    assertThat(result.getAffixClasses().getFirst().rules.get(0).condition(), equalTo("b"));
  }

  /** Taken from "prefix--suffix dependencies" from https://manpages.ubuntu.com/manpages/bionic/en/man5/hunspell.5.html */
  @Test
  void shouldParseContinuationClasses() {
    // given
    List<String> lines = List.of(
        "PFX P Y 1",
        "PFX P   0 un . [prefix_un]+",
        "",
        "SFX S Y 1",
        "SFX S   0 s . +PL",
        "",
        "SFX Q Y 1",
        "SFX Q   0 s . +3SGV",
        "",
        "SFX R Y 1",
        "SFX R   0 able/PS . +DER_V_ADJ_ABLE");

    // when
    ParsedAffixes result = parser.parseAffFile(lines.stream());

    // then
    assertThat(result.getAffixClasses(), hasSize(4));
    assertThat(result.getAffixClasses().get(0).flag, equalTo("P"));
    assertThat(result.getAffixClasses().get(1).flag, equalTo("S"));
    assertThat(result.getAffixClasses().get(2).flag, equalTo("Q"));

    assertThat(result.getAffixClasses().get(3).flag, equalTo("R"));
    ParsedAffixClass classR = result.getAffixClasses().get(3);
    assertThat(classR.rules, hasSize(1));
    assertThat(classR.rules.get(0).strip(), equalTo(""));
    assertThat(classR.rules.get(0).affix(), equalTo("able"));
    assertThat(classR.rules.get(0).continuationClasses(), contains("P", "S"));
    assertThat(classR.rules.get(0).condition(), equalTo("."));
  }

  @Test
  void shouldParseForbiddenWordAndNeedAffixClasses() {
    // given
    List<String> lines = List.of(
        "SET UTF-8",
        "FLAG num",
        "FORBIDDENWORD 36",
        "NEEDAFFIX 53",
        "",
        "PFX 12 Y 1",
        "PFX 12   0 un .");

    // when
    ParsedAffixes result = parser.parseAffFile(lines.stream());

    // then
    assertThat(result.getNeedAffixFlag(), equalTo("53"));
    assertThat(result.getForbiddenWordClass(), equalTo("36"));

    assertThat(result.getAffixClasses(), hasSize(1));
    ParsedAffixClass unPfx = result.getAffixClasses().get(0);
    assertThat(unPfx.flag, equalTo("12"));
    assertThat(unPfx.rules, hasSize(1));
    assertThat(unPfx.rules.get(0).strip(), equalTo(""));
    assertThat(unPfx.rules.get(0).affix(), equalTo("un"));
    assertThat(unPfx.rules.get(0).continuationClasses(), empty());
    assertThat(unPfx.rules.get(0).condition(), equalTo("."));
  }

  @Test
  void shouldSupportAffixesAndSuffixesWithSameName() {
    // given
    List<String> lines = List.of(
        "PFX A Y 1",
        "PFX A   0 re .  # Prefix",
        "",
        "SFX A Y 1",
        "SFX A   0 ed [^e]  # Suffix",
        "SFX A   0 d e");

    // when
    ParsedAffixes result = parser.parseAffFile(lines.stream());

    // then
    assertThat(result.getAffixClasses(), hasSize(2));
    ParsedAffixClass prefixClass = result.getAffixClasses().get(0);
    assertThat(prefixClass.rules, hasSize(1));
    ParsedAffixClass suffixClass = result.getAffixClasses().get(1);
    assertThat(suffixClass.rules, hasSize(2));
  }

  @Test
  void shouldParseAffixClassWithComment() {
    // given
    List<String> lines = List.of(
        "FLAG long",
        "",
        "SFX B3 Y 1 # this is a test suffix",
        "SFX B3   0 ing . # sample rule");

    // when
    ParsedAffixes result = parser.parseAffFile(lines.stream());

    // then
    assertThat(result.getAffixClasses(), hasSize(1));
    assertThat(result.getAffixClasses().getFirst().flag, equalTo("B3"));
    assertThat(result.getAffixClasses().getFirst().rules, hasSize(1));
    assertThat(result.getAffixClasses().getFirst().rules.getFirst().affix(), equalTo("ing"));
  }

  /** Excerpt from fr.aff. */
  @Test
  void shouldTrimAffixToEmptyForZeroWithContinuationClasses() {
    // given
    List<String> lines = List.of(
        "FLAG long",
        "PFX Um Y 29",
        "PFX Um 0 0/S. .",
        "PFX Um 0 l'exa . dp:le|la+");

    // when
    ParsedAffixes result = parser.parseAffFile(lines.stream());

    // then
    assertThat(result.getAffixClasses(), hasSize(1));
    assertThat(result.getAffixClasses().getFirst().flag, equalTo("Um"));
    assertThat(result.getAffixClasses().getFirst().rules, hasSize(2));

    ParsedAffixClass.Rule rule1 = result.getAffixClasses().getFirst().rules.get(0);
    assertThat(rule1.strip(), emptyString());
    assertThat(rule1.affix(), emptyString());
    assertThat(rule1.continuationClasses(), contains("S."));
    assertThat(rule1.condition(), equalTo("."));

    ParsedAffixClass.Rule rule2 = result.getAffixClasses().getFirst().rules.get(1);
    assertThat(rule2.strip(), emptyString());
    assertThat(rule2.affix(), equalTo("l'exa"));
    assertThat(rule2.continuationClasses(), empty());
    assertThat(rule2.condition(), equalTo("."));
  }

  /** Excerpt from nl.aff. */
  @Test
  void shouldExtractOnlyInCompoundFlag() {
    // given
    List<String> lines = List.of(
        "FLAG long",
        "ONLYINCOMPOUND Cx",
        "SFX Yb Y 6" ,
        "SFX Yb 0 je [^m]\t\t\tts:NN1r");

    // when
    ParsedAffixes result = parser.parseAffFile(lines.stream());

    // then
    assertThat(result.getForbiddenWordClass(), nullValue());
    assertThat(result.getOnlyInCompound(), equalTo("Cx"));

    assertThat(result.getAffixClasses(), hasSize(1));
    assertThat(result.getAffixClasses().getFirst().rules, hasSize(1));
  }
}
