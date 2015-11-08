package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class LanguageTest {

  @Test
  public void shouldHandleUnsetProperties() {
    Language lang = new Language("zxx", LATIN);

    assertThat(lang.getCode(), equalTo("zxx"));
    assertThat(lang.getAdditionalConsonants(), emptyArray());
    assertThat(lang.getAdditionalVowels(), emptyArray());
  }

  @Test
  public void shouldNotHaveLettersToPreserveIfNonApplicable() {
    Language lang = new Language("zxx", LATIN).setAdditionalVowels("ij")
        .setAdditionalConsonants("cs", "ny");

    assertThat(lang.getAdditionalVowels(), arrayWithSize(1));
    assertThat(lang.getAdditionalConsonants(), arrayWithSize(2));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionForUnknownLanguageCode() {
    Language.get("bogusCode");
  }
  
  @Test
  public void shouldGetLanguageWithoutHyphen() {
    Language zxxLang = new Language("zxx", LATIN);
    Language.add(zxxLang);
    
    Language result = Language.get("zxx-ww");
    
    assertThat(result, equalTo(zxxLang));
  }

}
