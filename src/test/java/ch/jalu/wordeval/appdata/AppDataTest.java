package ch.jalu.wordeval.appdata;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.language.Language;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link AppData}.
 */
class AppDataTest {

  private final AppData appData = new AppData();

  @Test
  void shouldReturnLanguage() {
    // given / when
    Language fr = appData.getLanguage("fr");

    // then
    assertThat(fr.getCode(), equalTo("fr"));
  }

  @Test
  void shouldThrowForUnknownLanguage() {
    // given / when
    IllegalStateException ex = assertThrows(IllegalStateException.class,
        () -> appData.getLanguage("xl"));

    // then
    assertThat(ex.getMessage(), equalTo("Unknown language code: xl"));
  }

  @Test
  void shouldReturnDictionary() {
    // given / when
    Dictionary fr = appData.getDictionary("en-us");

    // then
    assertThat(fr.getIdentifier(), equalTo("en-us"));
  }

  @Test
  void shouldThrowForUnknownDictionary() {
    // given / when
    IllegalStateException ex = assertThrows(IllegalStateException.class,
        () -> appData.getDictionary("mz"));

    // then
    assertThat(ex.getMessage(), equalTo("Unknown dictionary code: mz"));
  }

  @Test
  void shouldReturnAllDictionaries() {
    // given / when
    List<Dictionary> dictionaries = appData.getAllDictionaries();

    // then
    assertThat(dictionaries.size(), greaterThan(20));
    assertThat(dictionaries.size(), lessThan(30));
    assertThat(dictionaries.get(0).getIdentifier(), equalTo("af"));
  }
}
