package ch.ljacqu.wordeval.language;

import static ch.ljacqu.wordeval.language.Alphabet.LATIN;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
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
  
  @Test
  public void shouldReturnCharsToPreserve() {
    Language lang = new Language("zxx", LATIN)
      .setAdditionalConsonants("cs", "þ", "y")
      .setAdditionalVowels("w", "eu", "ø", "öy");

    assertThat(toCharList(lang.getCharsToPreserve()), containsInAnyOrder('þ', 'ø'));
  }

  @Test
  public void shouldReturnEmptyCharsToPreserve() {
    Language lang1 = new Language("zxx", LATIN);
    Language lang2 = new Language("zxx", LATIN)
      .setAdditionalConsonants("tt", "ff", "gg")
      .setAdditionalVowels("ii", "w", "øu");

    assertThat(toCharList(lang1.getCharsToPreserve()), empty());
    assertThat(toCharList(lang2.getCharsToPreserve()), empty());
  }

  private static List<Character> toCharList(String s) {
    return Arrays.asList(ArrayUtils.toObject(s.toCharArray()));
  }
}
