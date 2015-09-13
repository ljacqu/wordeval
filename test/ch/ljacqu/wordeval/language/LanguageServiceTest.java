package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import static ch.ljacqu.wordeval.language.Alphabet.CYRILLIC;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import ch.ljacqu.wordeval.language.LanguageService;

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
        "ff", "gg").setAdditionalVowels("ii", "w", "øu");

    assertThat(LanguageService.computeCharsToPreserve(lang1), empty());
    assertThat(LanguageService.computeCharsToPreserve(lang2), empty());
  }

}
