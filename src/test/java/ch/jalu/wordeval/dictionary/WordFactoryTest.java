package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.language.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Test for {@link WordFactory}.
 */
class WordFactoryTest {

  @Test
  void shouldKeepAdditionalLetters() {
    // given
    Language language = newLanguage("da")
        .additionalVowels("æ", "ø", "å")
        .build();
    String[] words = { "forsøgte erklære trådte", "Å ǿ én býr" };
    WordFactory builder = new WordFactory(language);

    // when
    Word[] result = new Word[2];
    result[0] = builder.createWordObject(words[0]);
    result[1] = builder.createWordObject(words[1]);

    // then
    assertThat(result[0].getWithoutAccents(), equalTo("forsøgte erklære trådte"));
    assertThat(result[1].getWithoutAccents(), equalTo("å ø en byr"));
  }

  @Test
  void shouldRemoveAllAccentsByDefault() {
    // given
    Language language = newLanguage("fr").build();
    WordFactory builder = new WordFactory(language);

    // when
    Word result = builder.createWordObject("ÉÑÀÇÏÔ");

    // then
    assertThat(result.getWithoutAccents(), equalTo("enacio"));
  }

  @Test
  void shouldUseLocaleForLowerCase() {
    // given
    Language language = newLanguage("tr").build();
    WordFactory builder = new WordFactory(language);

    // when
    Word result = builder.createWordObject("PRINÇE");

    // then
    assertThat(result.getLowercase(), equalTo("prınçe"));
  }

  @Test
  void shouldComputeWordOnlyForm() {
    // given
    Language language = newLanguage("cs")
        .additionalConsonants("č", "ř")
        .build();
    WordFactory builder = new WordFactory(language);

    // when
    Word result = builder.createWordObject("ČL-OV'ěk-ůŘ");

    // then
    assertThat(result.getWithoutAccentsWordCharsOnly(), equalTo("človekuř"));
  }

  @Test
  void shouldThrowForEmptyWord() {
    // given
    Language language = newLanguage("nl").build();
    WordFactory builder = new WordFactory(language);

    // when / then
    assertThrows(IllegalArgumentException.class, () -> builder.createWordObject(""));
  }

  @ParameterizedTest
  @MethodSource("getWordLanguagePairs")
  void shouldBuildWordFormsAsExpected(String word, Language language, Word expectedWord) {
    // given
    WordFactory wordFactory = new WordFactory(language);

    // when
    Word actualWord = wordFactory.createWordObject(word);

    // then
    assertThat(actualWord.getRaw(), equalTo(expectedWord.getRaw()));
    assertThat(actualWord.getLowercase(), equalTo(expectedWord.getLowercase()));
    assertThat(actualWord.getWithoutAccents(), equalTo(expectedWord.getWithoutAccents()));
    assertThat(actualWord.getWithoutAccentsWordCharsOnly(), equalTo(expectedWord.getWithoutAccentsWordCharsOnly()));
  }

  static Stream<Arguments> getWordLanguagePairs() {
    AppData appData = new AppData();
    Language da = appData.getDictionary("da").getLanguage();
    Language es = appData.getDictionary("es").getLanguage();
    Language fr = appData.getDictionary("fr").getLanguage();
    Language nb = appData.getDictionary("nb").getLanguage();
    Language tr = appData.getDictionary("tr").getLanguage();

    return Stream.of(
        Arguments.of("Loftshøjgård", da,
            createWord("Loftshøjgård", "loftshøjgård", "loftshøjgård", "loftshøjgård")),
        Arguments.of("cartagüeño", es,
            createWord("cartagüeño", "cartagüeño", "cartagueño", "cartagueño")),
        Arguments.of("d'être", fr,
            createWord("d'être", "d'être", "d'etre", "detre")),
        Arguments.of("armégevær", nb,
            createWord("armégevær", "armégevær", "armegevær", "armegevær")),
        Arguments.of("İnanç'la", tr,
            createWord("İnanç'la", "inanç'la", "inanç'la", "inançla"))
    );
  }

  private static Word createWord(String raw, String lower, String noAccents, String noAccentsWordCharsOnly) {
    Word word = new Word();
    word.setRaw(raw);
    word.setLowercase(lower);
    word.setWithoutAccents(noAccents);
    word.setWithoutAccentsWordCharsOnly(noAccentsWordCharsOnly);
    return word;
  }
}
