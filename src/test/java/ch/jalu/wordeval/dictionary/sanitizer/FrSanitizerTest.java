package ch.jalu.wordeval.dictionary.sanitizer;

import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.runners.DictionaryProcessor;
import com.google.common.collect.Sets;
import lombok.extern.log4j.Log4j2;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.jalu.wordeval.TestUtil.assumeThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

/**
 * Test for {@link FrSanitizer}.
 */
@Log4j2
class FrSanitizerTest {

  private static Dictionary frDictionary;
  
  @BeforeAll
  static void initData() {
    frDictionary = new AppData().getDictionary("fr");
  }

  @Test
  void shouldFindTheGivenWords() {
    if (!TestUtil.doesDictionaryFileExist(frDictionary)) {
      log.warn("Skipping Fr sanitizer test because dictionary doesn't exist");
      assumeThat(true, equalTo(false));
    }

    // given / when
    Set<String> words = DictionaryProcessor.readAllWords(frDictionary).stream()
      .map(Word::getRaw)
      .collect(Collectors.toSet());

    // then
    assertThat(words, hasItems("civil", "cil", "six")); // kind of looks like roman numerals...
    assertThat(words, hasItems("notre", "ou", "parmi", "te")); // from lines that don't have a / delimiter
    assertThat(words, hasItems("y", "zaouïa"));
    assertThat(words, hasItems("socratiser", "démurer", "boire")); // has digits after the delimiter

    assertThat(words, hasNoneItems("II", "III", "IIIe", "IIIes", "IIIᵉ", "IIIᵉˢ", "IV", "IVe", "IVᵉ", "IXes"));
    assertThat(words, hasNoneItems("brrr", "grrr", "pfft"));
    assertThat(words, hasNoneItems("-"));
  }

  private static Matcher<Set<String>> hasNoneItems(String... entries) {
    return new TypeSafeMatcher<>() {

      @Override
      protected boolean matchesSafely(Set<String> item) {
        return Sets.intersection(item, Set.of(entries)).isEmpty();
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("Set without any of: " + Arrays.toString(entries));
      }

      @Override
      protected void describeMismatchSafely(Set<String> item, Description mismatchDescription) {
        String foundEntries = String.join(", ", Sets.intersection(item, Set.of(entries)));
        mismatchDescription.appendText("Set with items: " + foundEntries);
      }
    };
  }
}
