package ch.jalu.wordeval.dictionary.sanitizer;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.Word;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

/**
 * Test for {@link ItSanitizer}.
 */
class ItSanitizerTest extends AbstractSanitizerTest {

  private Dictionary itDictionary;

  @BeforeEach
  void initDictionary() {
    itDictionary = getDictionary("it");
  }

  @Test
  void shouldSanitizeWords() {
    assumeDictionaryFileExists(itDictionary);

    // given / when
    Set<String> words = dictionaryService.readAllWords(itDictionary).stream()
        .map(Word::getRaw)
        .collect(Collectors.toSet());

    // then
    assertThat(words, hasItems("abaco", "Abel", "lesero", "l'estese"));
    List<String> wordsWithCopyright = words.stream()
        .filter(word -> word.contains("opyright"))
        .toList();
    assertThat(wordsWithCopyright, contains("copyright")); // There are some copyright easter eggs. Make sure we only keep "copyright" itself
    assertThat(words, not(hasItem("ziiiziiizxxivziiizmmxi")));
  }

  @Test
  void shouldSanitize() {
    // given
    List<String> lines = getLines();

    // when
    List<String> words = dictionaryService.processAllWords(itDictionary, lines).stream()
        .map(Word::getRaw)
        .toList();

    // then
    assertThat(words, contains("ash", "cat", "emu", "frog-fish-ferret", "gator", "joker"));
  }

  private static List<String> getLines() {
    return List.of(
        "420",
        "/ Header comment",
        "/ should be ignored",
        "ash/APC",
        "cat",
        "ziiiziiizxxivziiizmmxi",
        "XIX",
        "emu/XIX",
        "frog-fish-ferret/PP3 po:an",
        "CopyrightSomeFunLineHere",
        "gator/ee",
        "CopyrightOtherFunLine",
        "joker/e"
    );
  }
}
