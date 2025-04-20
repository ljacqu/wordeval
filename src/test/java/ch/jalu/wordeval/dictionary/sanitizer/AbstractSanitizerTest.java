package ch.jalu.wordeval.dictionary.sanitizer;

import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.config.BaseConfiguration;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.DictionaryService;
import com.google.common.collect.Sets;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Common type for dictionary sanitizer tests.
 */
@SpringJUnitConfig(classes = BaseConfiguration.class)
abstract class AbstractSanitizerTest {

  @Autowired
  private AppData appData;

  @Autowired
  protected DictionaryService dictionaryService;

  /**
   * Gets the dictionary with the given code. Throws an exception if it does not exist.
   *
   * @param code the language code to look up
   * @return the specified dictionary
   */
  protected Dictionary getDictionary(String code) {
    return appData.getDictionary(code);
  }

  /**
   * Throws a JUnit assumption exception if the file the dictionary object points to does not exist.
   *
   * @param dictionary the dictionary whose file should be checked
   */
  protected static void assumeDictionaryFileExists(Dictionary dictionary) {
    assumeTrue(TestUtil.doesDictionaryFileExist(dictionary),
        () -> "Skipping test because the dictionary file doesn't exist");
  }

  /**
   * Returns a matcher that only evaluates successfully if <b>none</b> of the given entries were found.
   *
   * @param entries the entries which should not be part of the collection
   * @return matcher ensuring no specified value is present
   */
  protected static Matcher<Set<String>> hasNoneItems(String... entries) {
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
