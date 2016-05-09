package ch.jalu.wordeval.language;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link Language}.
 */
public class LanguageTest {

  @Test
  public void shouldHandleUnsetProperties() {
    Language lang = newLanguage("zxx");

    assertThat(lang.getCode(), equalTo("zxx"));
    assertThat(lang.getAdditionalConsonants(), emptyArray());
    assertThat(lang.getAdditionalVowels(), emptyArray());
  }

  @Test
  public void shouldNotHaveLettersToPreserveIfNonApplicable() {
    Language lang = newLanguage("zxx").setAdditionalVowels("ij")
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
    Language zxxLang = newLanguage("zxx");
    Language.add(zxxLang);
    
    Language result = Language.get("zxx-ww");
    
    assertThat(result, equalTo(zxxLang));
  }
  
  @Test
  public void shouldReturnCharsToPreserve() {
    Language lang = newLanguage("zxx")
      .setAdditionalConsonants("cs", "þ", "y")
      .setAdditionalVowels("w", "eu", "ø", "öy");

    assertThat(toCharList(lang.getCharsToPreserve()), containsInAnyOrder('þ', 'ø'));
  }

  @Test
  public void shouldReturnEmptyCharsToPreserve() {
    Language lang1 = newLanguage("zxx");
    Language lang2 = newLanguage("zxx")
      .setAdditionalConsonants("tt", "ff", "gg")
      .setAdditionalVowels("ii", "w", "øu");

    assertThat(toCharList(lang1.getCharsToPreserve()), empty());
    assertThat(toCharList(lang2.getCharsToPreserve()), empty());
  }

  private static List<Character> toCharList(String s) {
    return Arrays.asList(ArrayUtils.toObject(s.toCharArray()));
  }
}
