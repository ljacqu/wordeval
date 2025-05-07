package ch.jalu.wordeval.dictionary.hunspell.lineprocessor;

import ch.jalu.wordeval.dictionary.HunspellDictionary;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.dictionary.hunspell.HunspellDictionaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.jalu.wordeval.TestUtil.largeCollectionHasItems;
import static ch.jalu.wordeval.TestUtil.largeCollectionHasNoneItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Test for {@link ItLineProcessor}.
 */
class ItLineProcessorTest extends AbstractLineProcessorTest {

  private HunspellDictionary itDictionary;

  @Autowired
  private HunspellDictionaryService hunspellDictionaryService;

  @BeforeEach
  void initDictionary() {
    itDictionary = getDictionary("it");
  }

  @Test
  void shouldSplitAndSanitizeWords() {
    assumeDictionaryFileExists(itDictionary);

    // given / when
    Set<String> words = dictionaryService.readAllWords(itDictionary).stream()
        .map(Word::getRaw)
        .collect(Collectors.toSet());

    // then
    assertThat(words, largeCollectionHasItems("abaco", "Abel", "lesero", "l'estese"));
    List<String> wordsWithCopyright = words.stream()
        .filter(word -> word.contains("opyright"))
        .toList();
    assertThat(wordsWithCopyright, contains("copyright")); // There are some copyright Easter eggs. Make sure we only keep "copyright" itself
    assertThat(words, largeCollectionHasNoneItems("ziiiziiizxxivziiizmmxi"));
  }

  @Test
  void shouldSplitAndSanitize() {
    // given
    List<String> lines = getLines();

    // when
    List<String> words = hunspellDictionaryService.loadAllWords(lines.stream(), itDictionary).toList();

    // then
    assertThat(words, contains("ash", "cat", "demo", "frog-fish-ferret", "gator", "joker"));
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
        "demo/XIX",
        "frog-fish–ferret/PP3 po:an",
        "CopyrightSomeFunLineHere",
        "gator/ww",
        "CopyrightOtherFunLine",
        "joker/w"
    );
  }
}
