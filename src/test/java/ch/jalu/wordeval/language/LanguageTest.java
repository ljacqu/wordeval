package ch.jalu.wordeval.language;

import org.junit.jupiter.api.Test;

import java.util.List;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;


/**
 * Test for {@link Language}.
 */
class LanguageTest {

  @Test
  void shouldHandleUnsetProperties() {
    Language lang = newLanguage("zxx").build();

    assertThat(lang.getCode(), equalTo("zxx"));
    assertThat(lang.getConsonants(), equalTo(Alphabet.LATIN.getDefaultConsonants()));
    assertThat(lang.getVowels(), equalTo(Alphabet.LATIN.getDefaultVowels()));
  }

  @Test
  void shouldIncludeAdditionalVowelsAndConsonants() {
    Language lang = newLanguage("zxx")
        .additionalVowels("ij")
        .additionalConsonants("cs", "ny")
        .build();

    assertThat(lang.getVowels(), contains("a", "e", "i", "o", "u", "y", "ij"));
    assertThat(lang.getConsonants(), hasItems("f", "g", "h", "w", "x", "z", "cs", "ny")); // just check some samples
  }
  
  @Test
  void shouldReturnCharsToPreserve() {
    Language lang = newLanguage("zxx")
      .additionalConsonants("cs", "þ", "y")
      .additionalVowels("w", "eu", "ø", "öy").build();

    assertThat(lang.getCharsToPreserve(), equalTo("øþ"));
  }

  @Test
  void shouldGetLettersWithAdditional() {
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
  void shouldRemoveLettersFromDefaultList() {
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
  void shouldReturnEmptyCharsToPreserve() {
    Language lang1 = newLanguage("zxx").build();
    Language lang2 = newLanguage("zxx")
      .additionalConsonants("tt", "ff", "gg")
      .additionalVowels("ii", "w", "øu").build();

    assertThat(lang1.getCharsToPreserve(), emptyString());
    assertThat(lang2.getCharsToPreserve(), emptyString());
  }
}
