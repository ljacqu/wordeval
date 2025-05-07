package ch.jalu.wordeval.dictionary.hunspell.lineprocessor;

import ch.jalu.wordeval.dictionary.HunspellDictionary;
import ch.jalu.wordeval.dictionary.Word;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.jalu.wordeval.TestUtil.largeCollectionHasItems;
import static ch.jalu.wordeval.TestUtil.largeCollectionHasNoneItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Test for {@link NlLineProcessor}.
 */
class NlLineProcessorTest extends AbstractLineProcessorTest {

  private final NlLineProcessor nlLineProcessor = new NlLineProcessor();

  @Test
  void shouldSplitAndSanitizeWords() {
    HunspellDictionary nlDictionary = getDictionary("nl");
    assumeDictionaryFileExists(nlDictionary);

    // given / when
    Set<String> words = dictionaryService.readAllWords(nlDictionary).stream()
        .map(Word::getRaw)
        .collect(Collectors.toSet());

    // then
    assertThat(words, largeCollectionHasItems("abdijbier", "lekkerder", "zeeën", "IJzermijn", "pygmeeën", "pruttelarijen"));
    assertThat(words, largeCollectionHasNoneItems("o.a.", "C++", "aanw.", "ww.", "Costa Rica"));
  }

  @Test
  void shouldSplitAndSanitize() {
    // given
    List<String> lines = List.of("kat", "km\\/h", "o.a.", "hond", "m\\/s", "C++", "appel",
        "Puerto Rica", "Puerto Ricaans/Aa", "Ĳ'tje/Za", "D.V./ClCw", "pĳn/ABC");

    // when
    List<String> result = processLines(lines, nlLineProcessor);

    // then
    assertThat(result, contains("kat", "hond", "appel", "IJ'tje", "pijn"));
  }
}
