package ch.jalu.wordeval.dictionary.sanitizer;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.runners.DictionaryProcessor;
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

  @Test
  void shouldSanitizeWords() {
    // given
    Dictionary dictionary = createDictionaryIfPossible("it");

    // when
    Set<String> words = DictionaryProcessor.readAllWords(dictionary).stream()
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
}