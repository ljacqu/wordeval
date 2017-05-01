package ch.jalu.wordeval.language;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link Language}.
 */
public class LanguageTest {

  @Test
  public void shouldHandleUnsetProperties() {
    Language lang = newLanguage("zxx").build();

    assertThat(lang.getCode(), equalTo("zxx"));
    assertThat(lang.getAdditionalConsonants(), empty());
    assertThat(lang.getAdditionalVowels(), empty());
  }

  @Test
  public void shouldNotHaveLettersToPreserveIfNonApplicable() {
    Language lang = newLanguage("zxx")
        .additionalVowels("ij")
        .additionalConsonants("cs", "ny")
        .build();

    assertThat(lang.getAdditionalVowels(), hasSize(1));
    assertThat(lang.getAdditionalConsonants(), hasSize(2));
  }
  
  @Test
  public void shouldReturnCharsToPreserve() {
    Language lang = newLanguage("zxx")
      .additionalConsonants("cs", "þ", "y")
      .additionalVowels("w", "eu", "ø", "öy").build();

    assertThat(toCharList(lang.getCharsToPreserve()), containsInAnyOrder('þ', 'ø'));
  }

  @Test
  public void shouldGetLettersWithAdditional() {
    // given
    Language language = Language.builder("zxx", "", Alphabet.CYRILLIC)
        .additionalConsonants("rz", "s")
        .additionalVowels("u", "èö").build();

    // when
    List<String> vowels = language.getVowels();
    List<String> consonants = language.getConsonants();

    // then
    // Check the additional letters + a few other random ones
    assertThat(vowels, hasItems("u", "èö", "и", "я"));
    assertThat(consonants, hasItems("rz", "s", "т", "ж"));
  }

  @Test
  public void shouldRemoveLettersFromDefaultList() {
    // given
    Language language = Language.builder("zxx", "", Alphabet.LATIN)
        .additionalVowels("w")
        .lettersToRemove("w").build();

    // when
    List<String> vowels = language.getVowels();
    List<String> consonants = language.getConsonants();

    // then
    assertThat(vowels, hasItem("w"));
    assertThat(consonants, not(hasItem("w")));
    assertThat(consonants, hasItems("c", "g", "v", "z"));
  }

  @Test
  public void shouldReturnEmptyCharsToPreserve() {
    Language lang1 = newLanguage("zxx").build();
    Language lang2 = newLanguage("zxx")
      .additionalConsonants("tt", "ff", "gg")
      .additionalVowels("ii", "w", "øu").build();

    assertThat(toCharList(lang1.getCharsToPreserve()), empty());
    assertThat(toCharList(lang2.getCharsToPreserve()), empty());
  }

  private static List<Character> toCharList(String s) {
    return Arrays.asList(ArrayUtils.toObject(s.toCharArray()));
  }
}
