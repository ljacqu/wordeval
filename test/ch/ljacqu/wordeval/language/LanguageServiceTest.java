package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.TestUtil.newLanguage;
import static ch.ljacqu.wordeval.language.Alphabet.CYRILLIC;
import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import static ch.ljacqu.wordeval.language.LetterType.CONSONANTS;
import static ch.ljacqu.wordeval.language.LetterType.VOWELS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class LanguageServiceTest {

  @Test
  public void shouldRemoveDiacritics() {
    String[] givenWords = { "křižáků", "nőstényét", "iš vėlyvojo Jų",
        "mogą trwać występy koreańskich że", "požiūriu" };
    String[] expected = { "krizaku", "nostenyet", "is velyvojo Ju",
        "moga trwac wystepy koreanskich ze", "poziuriu" };

    for (int i = 0; i < givenWords.length; ++i) {
      assertThat(LanguageService.removeAccentsFromWord(givenWords[i], LATIN),
          equalTo(expected[i]));
    }
  }

  @Test
  public void shouldRemoveDiacriticsForCyrillic() {
    String[] words = { "ѝ", "призёр", "Менько́в", "аўтар", "куќата",
        "військової" };
    String[] expected = { "и", "призёр", "Меньков", "аўтар", "куќата",
        "військової" };

    List<String> result = new ArrayList<>();
    for (String word : words) {
      result.add(LanguageService.removeAccentsFromWord(word, CYRILLIC));
    }
    assertThat(result.toArray(), equalTo(expected));
  }

  @Test
  public void shouldGetLettersWithAdditional() {
    Language language = new Language("zxx", "", CYRILLIC)
        .setAdditionalConsonants("rz", "s")
        .setAdditionalVowels("u", "èö");

    List<String> vowels = LanguageService.getLetters(VOWELS, language);
    List<String> consonants = LanguageService.getLetters(CONSONANTS, language);

    // Check the additional letters + a few other random ones
    assertThat(vowels, hasItems("u", "èö", "и", "я"));
    assertThat(consonants, hasItems("rz", "s", "т", "ж"));
  }

  @Test
  public void shouldRemoveLettersFromDefaultList() {
    Language lang = newLanguage("zxx")
        .setAdditionalVowels("w")
        .setLettersToRemove("w");

    List<String> vowels = LanguageService.getLetters(VOWELS, lang);
    List<String> consonants = LanguageService.getLetters(CONSONANTS, lang);

    assertThat(vowels, hasItem("w"));
    assertThat(consonants, not(hasItem("w")));
    assertThat(consonants, hasItems("c", "g", "v", "z"));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowForUnknownAlphabet() {
    Language lang = new Language("zxx", "", null);
    LanguageService.getLetters(VOWELS, lang);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowForUnknownAlphabet2() {
    Language lang = new Language("zxx", "", null);
    LanguageService.getLetters(CONSONANTS, lang);
  }

}
