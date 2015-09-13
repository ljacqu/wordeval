package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import ch.ljacqu.wordeval.language.LanguageService;

public class LanguageServiceTest {

  @Test
  public void shouldRemoveDiacritics() {
    String[] givenWords = { "křižáků", "nőstényét", "iš vėlyvojo Jų",
        "długo mogą trwać występy koreańskich że", "požiūriu" };
    String[] expected = { "krizaku", "nostenyet", "is velyvojo Ju",
        "dlugo moga trwac wystepy koreanskich ze", "poziuriu" };

    for (int i = 0; i < givenWords.length; ++i) {
      assertThat(LanguageService.removeAccentsFromWord(givenWords[i]),
          equalTo(expected[i]));
    }
  }

  @Test
  public void shouldReturnCharsToPreserve() {
    Language lang = new Language("zxx", LATIN).setAdditionalConsonants("cs",
        "þ", "y").setAdditionalVowels("w", "eu", "ø", "öy");

    assertThat(LanguageService.computeCharsToPreserve(lang),
        containsInAnyOrder('þ', 'ø'));
  }

  @Test
  public void shouldReturnEmptyCharsToPreserve() {
    Language lang1 = new Language("zxx", LATIN);
    Language lang2 = new Language("zxx", LATIN).setAdditionalConsonants("tt",
        "ff", "gg").setAdditionalVowels("ii", "w", "eu");

    assertThat(LanguageService.computeCharsToPreserve(lang1), empty());
    assertThat(LanguageService.computeCharsToPreserve(lang2), empty());
  }

}
