package ch.jalu.wordeval.language;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link LanguageService}.
 */
public class LanguageServiceTest {

  @Test
  public void shouldRemoveDiacritics() {
    String[] givenWords = {
        "křižáků", "nőstényét", "iš vėlyvojo Jų", "mogą trwać występy koreańskich że", "požiūriu" };
    String[] expected = {
        "krizaku", "nostenyet", "is velyvojo Ju", "moga trwac wystepy koreanskich ze", "poziuriu" };

    for (int i = 0; i < givenWords.length; ++i) {
      assertThat(LanguageService.removeAccentsFromWord(givenWords[i], Alphabet.LATIN),
          equalTo(expected[i]));
    }
  }

  @Test
  public void shouldRemoveDiacriticsForCyrillic() {
    // given
    String[] words = { "ѝ", "призёр", "Менько́в", "аўтар", "куќата", "військової" };

    // when
    String[] result = Arrays.stream(words)
        .map(word -> LanguageService.removeAccentsFromWord(word, Alphabet.CYRILLIC))
        .toArray(String[]::new);

    // then
    String[] expected = { "и", "призёр", "Меньков", "аўтар", "куќата", "військової" };
    assertThat(result, equalTo(expected));
  }

  @Test
  public void shouldGetLettersWithAdditional() {
    // given
    Language language = Language.builder()
        .name("").code("zxx").alphabet(Alphabet.CYRILLIC)
        .additionalConsonants("rz", "s")
        .additionalVowels("u", "èö").build();

    // when
    List<String> vowels = LanguageService.getLetters(LetterType.VOWELS, language);
    List<String> consonants = LanguageService.getLetters(LetterType.CONSONANTS, language);

    // then
    // Check the additional letters + a few other random ones
    assertThat(vowels, hasItems("u", "èö", "и", "я"));
    assertThat(consonants, hasItems("rz", "s", "т", "ж"));
  }

  @Test
  public void shouldRemoveLettersFromDefaultList() {
    Language lang = Language.builder()
        .code("zxx").name("").alphabet(Alphabet.LATIN)
        .additionalVowels("w")
        .lettersToRemove("w").build();

    List<String> vowels = LanguageService.getLetters(LetterType.VOWELS, lang);
    List<String> consonants = LanguageService.getLetters(LetterType.CONSONANTS, lang);

    assertThat(vowels, hasItem("w"));
    assertThat(consonants, not(hasItem("w")));
    assertThat(consonants, hasItems("c", "g", "v", "z"));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowForUnknownAlphabet() {
    Language lang = mock(Language.class);
    given(lang.getAlphabet()).willReturn(null);
    LanguageService.getLetters(LetterType.VOWELS, lang);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowForUnknownAlphabet2() {
    Language lang = mock(Language.class);
    given(lang.getAlphabet()).willReturn(null);
    LanguageService.getLetters(LetterType.CONSONANTS, lang);
  }

}
