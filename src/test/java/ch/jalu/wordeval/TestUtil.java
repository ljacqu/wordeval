package ch.jalu.wordeval;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods for the tests.
 */
public final class TestUtil {

  private TestUtil() {
  }
  
  /**
   * Returns whether a dictionary's file exists or not.
   *
   * @param dictionary the dictionary to verify
   * @return true if the file exists, false otherwise
   */
  public static boolean doesDictionaryFileExist(Dictionary dictionary) {
    return Files.exists(Paths.get(dictionary.getFile()));
  }

  /**
   * Initializes a new Language builder with the given code and the Latin alphabet.
   *
   * @param code the language code
   * @return the Language builder
   */
  public static Language.Builder newLanguage(String code) {
    return newLanguage(code, Alphabet.LATIN);
  }

  /**
   * Initializes a new Language builder with the given code and alphabet.
   *
   * @param code the language code
   * @param alphabet the alphabet
   * @return the generated Language builder
   */
  public static Language.Builder newLanguage(String code, Alphabet alphabet) {
    return Language.builder(code, "", alphabet);
  }

  /**
   * Returns a matcher like {@link org.hamcrest.Matchers#hasItems} that evaluates positively if the collection
   * contains all provided items, but it may have more items. The difference is that in the case of failure,
   * the actual collection is not included in its entirety in the error message.
   *
   * @param expectedItems items expected to be part of the collection
   * @return collection matcher for the expected items
   */
  public static Matcher<Collection<String>> largeCollectionHasItems(String... expectedItems) {
    return new TypeSafeMatcher<>() {

      @Override
      protected boolean matchesSafely(Collection<String> actualCollection) {
        for (String expectedItem : expectedItems) {
          if (!actualCollection.contains(expectedItem)) {
            return false;
          }
        }
        return true;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("Collection with items: " + Arrays.toString(expectedItems));
      }

      @Override
      protected void describeMismatchSafely(Collection<String> actualCollection, Description mismatchDescription) {
        if (actualCollection.size() < 10) {
          super.describeMismatchSafely(actualCollection, mismatchDescription);
        } else {
          List<String> missingItems = Arrays.stream(expectedItems)
              .filter(item -> !actualCollection.contains(item))
              .toList();
          mismatchDescription.appendText("was collection of size " + actualCollection.size()
              + " missing expected items: " + String.join(", ", missingItems));
        }
      }
    };
  }

  /**
   * Returns a matcher that evaluates positively if the collection does not contain any of the provided items.
   * The difference to standard Hamcrest matchers is that the collection to test is not printed in its entirety
   * if the matcher does not evaluate successfully.
   *
   * @param expectedAbsentItems all items the collection may not have for the matcher to be successful
   * @return collection matcher for the items which should not be present
   */
  public static Matcher<Collection<String>> largeCollectionHasNoneItems(String... expectedAbsentItems) {
    return new TypeSafeMatcher<>() {

      @Override
      protected boolean matchesSafely(Collection<String> actualCollection) {
        for (String expectedItem : expectedAbsentItems) {
          if (actualCollection.contains(expectedItem)) {
            return false;
          }
        }
        return true;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("Collection without items: " + Arrays.toString(expectedAbsentItems));
      }

      @Override
      protected void describeMismatchSafely(Collection<String> actualCollection, Description mismatchDescription) {
        if (actualCollection.size() < 10) {
          super.describeMismatchSafely(actualCollection, mismatchDescription);
        } else {
          List<String> presentItems = Arrays.stream(expectedAbsentItems)
              .filter(actualCollection::contains)
              .toList();
          mismatchDescription.appendText("was collection of size " + actualCollection.size()
              + " with items expected NOT to be present: " + String.join(", ", presentItems));
        }
      }
    };
  }
}
