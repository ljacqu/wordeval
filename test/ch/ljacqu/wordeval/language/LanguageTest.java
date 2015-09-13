package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class LanguageTest {

  @Test
  public void shouldRegisterLettersToPreserve() {
    Language lang = new Language("zxx", LATIN).setAdditionalConsonants("cs",
        "þ", "y").setAdditionalVowels("w", "eu", "ø", "öy");

    assertThat(lang.getCharsToPreserve().toArray(),
        arrayContainingInAnyOrder('þ', 'ø'));
  }

  @Test
  public void shouldHandleUnsetProperties() {
    Language lang = new Language("zxx", LATIN);

    assertThat(lang.getCode(), equalTo("zxx"));
    assertThat(lang.getAdditionalConsonants(), emptyArray());
    assertThat(lang.getAdditionalVowels(), emptyArray());
    assertThat(lang.getCharsToPreserve().toArray(), emptyArray());
  }

  @Test
  public void shouldNotHaveLettersToPreserveIfNonApplicable() {
    Language lang = new Language("zxx", LATIN).setAdditionalConsonants("cs",
        "ny").setAdditionalVowels("ij");

    assertThat(lang.getCharsToPreserve().toArray(), emptyArray());
    assertThat(lang.getAdditionalVowels(), arrayWithSize(1));
    assertThat(lang.getAdditionalConsonants(), arrayWithSize(2));
  }

}
