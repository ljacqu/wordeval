package ch.jalu.wordeval.dictionary.sanitizer;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.DictionaryProcessor;
import ch.jalu.wordeval.dictionary.Word;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

/**
 * Test for the {@link HuSanitizer Hungarian dictionary} (which has custom sanitation).
 */
class HuSanitizerTest extends AbstractSanitizerTest {

  private Dictionary huDictionary;

  @BeforeEach
  void initDictionary() {
    huDictionary = getDictionary("hu");
  }

  @Test
  void shouldFindTheGivenWords() {
    assumeDictionaryFileExists(huDictionary);

    // given / when
    Set<String> words = DictionaryProcessor.readAllWords(huDictionary).stream()
      .map(Word::getRaw)
      .collect(Collectors.toSet());

    // then
    // should receive the words between Roman numerals
    assertThat(words, hasItems("csomóan", "csomó", "háromnegyed", "harmad",
      "kilenced", "milliomod", "trilliomod"));
    // should receive the split second words in two-word entries
    assertThat(words, hasItems("alak", "kor", "közben", "módra", "szer", "vég",
      "vége", "végén", "végi", "vevő", "cél"));
    // should get other words that have special treatment
    assertThat(words, hasItems("csak", "azért", "úti", "fő", "is"));
  }
}
