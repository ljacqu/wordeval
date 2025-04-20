package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test for {@link LanguageData}.
 */
class LanguageDataTest {

  @Test
  void shouldReturnAllLanguageConstants() {
    // given
    Set<Language> allLanguageConstants = Arrays.stream(LanguageData.class.getDeclaredFields())
        .filter(field -> field.getType().equals(Language.class))
        .map(field -> (Language) ReflectionTestUtils.getField(LanguageData.class, field.getName()))
        .collect(Collectors.toSet());

    // when
    Set<Language> returnedLanguages = LanguageData.streamThroughAll()
        .collect(Collectors.toSet());

    // then
    assertThat(returnedLanguages, equalTo(allLanguageConstants));
  }

  @ParameterizedTest
  @MethodSource("languageCodes")
  void shouldReturnLanguageByCode(String code) {
    // given / when
    Language language = LanguageData.getOrThrow(code);

    // then
    assertThat(language.getCode(), equalTo(code));
  }

  @Test
  void shouldHaveConsistentCodes() {
    // given
    LanguageData.streamThroughAll().forEach(lang -> {

      // when / then
      assertThat(lang.getCode(), matchesPattern("[a-z]{2}(-[a-z]{1,8})?"));
    });
  }

  @Test
  void shouldThrowForUnknownLanguage() {
    // given / when
    IllegalStateException ex = assertThrows(IllegalStateException.class,
        () -> LanguageData.getOrThrow("kx"));

    // then
    assertThat(ex.getMessage(), equalTo("Unknown language code: kx"));
  }

  @Test
  void shouldReturnNullForUnknownLanguage() {
    // given / when
    Language result = LanguageData.getOrNull("kx");

    // then
    assertThat(result, nullValue());
  }

  @ParameterizedTest
  @MethodSource("languageCodes")
  void shouldHaveWellDefinedLetters(String code) {
    // given
    Language language = LanguageData.getOrThrow(code);

    Set<String> vowels = new HashSet<>(language.getVowels());
    Set<String> consonants = new HashSet<>(language.getConsonants());

    Set<String> vowelAndConsonants = Sets.intersection(vowels, consonants);
    if (!vowelAndConsonants.isEmpty()) {
      fail("Found letters held as vowel AND consonant: " + vowelAndConsonants);
    }

    Set<String> allLetters = Stream.concat(vowels.stream(), consonants.stream())
        .collect(Collectors.toSet());
    streamThroughAllLetters(language.getAlphabet()).forEach(letter -> {
      assertThat(allLetters, hasItem(letter));
    });
  }

  static Stream<String> languageCodes() {
    return LanguageData.streamThroughAll()
        .map(Language::getCode);
  }

  private static Stream<String> streamThroughAllLetters(Alphabet alphabet) {
    return Stream.concat(alphabet.getDefaultConsonants().stream(), alphabet.getDefaultVowels().stream());
  }
}
