package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.dictionary.Dictionary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link DictionaryData}.
 */
class DictionaryDataTest {

  @Test
  void shouldReturnAllDictionaryConstants() {
    // given
    Set<Dictionary> allDictionaryConstants = Arrays.stream(DictionaryData.class.getDeclaredFields())
        .filter(field -> field.getType().equals(Dictionary.class))
        .map(field -> (Dictionary) ReflectionTestUtils.getField(DictionaryData.class, field.getName()))
        .collect(Collectors.toSet());

    // when
    Set<Dictionary> returnedDictionaries = DictionaryData.streamThroughAll()
        .collect(Collectors.toSet());

    // then
    assertThat(returnedDictionaries, equalTo(allDictionaryConstants));
  }

  @ParameterizedTest
  @MethodSource("dictionaryCodes")
  void shouldReturnDictionaryByCode(String code) {
    // given / when
    Dictionary dictionary = DictionaryData.getOrThrow(code);

    // then
    assertThat(dictionary.getIdentifier(), equalTo(code));
  }

  @Test
  void shouldHaveConsistentCodes() {
    // given
    DictionaryData.streamThroughAll().forEach(dict -> {

      // when / then
      assertThat(dict.getIdentifier(), matchesPattern("[a-z]{2}(-[a-z]{1,8})?"));
    });
  }

  @Test
  void shouldThrowForUnknownDictionary() {
    // given / when
    IllegalStateException ex = assertThrows(IllegalStateException.class,
        () -> DictionaryData.getOrThrow("xf"));

    // then
    assertThat(ex.getMessage(), equalTo("Unknown dictionary code: xf"));
  }

  @Test
  void shouldReturnNullForUnknownDictionary() {
    // given / when
    Dictionary result = DictionaryData.getOrNull("xf");

    // then
    assertThat(result, nullValue());
  }

  static Stream<String> dictionaryCodes() {
    return DictionaryData.streamThroughAll()
        .map(Dictionary::getIdentifier);
  }
}
