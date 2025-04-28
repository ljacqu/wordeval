package ch.jalu.wordeval.dictionary.hunspell.sanitizer;

import ch.jalu.wordeval.dictionary.HunspellDictionary;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.dictionary.hunspell.HunspellDictionaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
@Disabled // TODO: Fix endless loops -- assertion failures toString take too long :)
class ItSanitizerTest extends AbstractSanitizerTest {

  private HunspellDictionary itDictionary;

  @Autowired
  private HunspellDictionaryService hunspellDictionaryService;

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
    List<String> words = hunspellDictionaryService.loadAllWords(lines.stream(), itDictionary).toList();

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
