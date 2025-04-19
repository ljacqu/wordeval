package ch.jalu.wordeval.dictionary.sanitizer;

import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.runners.DictionaryProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;

/**
 * Test for {@link FrSanitizer}.
 */
class FrSanitizerTest extends AbstractSanitizerTest {

  private static Dictionary frDictionary;

  @BeforeAll
  static void initData() {
    frDictionary = createDictionary("fr");
  }

  @Test
  void shouldFindTheGivenWords() {
    assumeDictionaryFileExists(frDictionary);

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

  @Test
  void shouldSanitize() {
    // given
    List<String> lines = getLines();

    // when
    List<String> words = DictionaryProcessor.processAllWords(frDictionary, lines).stream()
        .map(Word::getRaw)
        .toList();

    // then
    assertThat(words, contains("able", "bin", "cat", "dog", "emu", "frog-fish-ferret", "gator", "joker"));
  }

  private static List<String> getLines() {
    return List.of(
        "able/APC",
        "bin/44P po:test",
        "cat",
        "dog po:an",
        "XIXe",
        "- po:ponc po:sign",
        "emu/XIX",
        "frog-fish-ferret/PP3 po:an",
        "pfft",
        "grrr",
        "XXXIIes",
        "gator/ee",
        "H₆Pl₉",
        "iffᵉ",
        "joker/e",
        "Δt",
        "some",
        "thing",
        "else"
    );
  }
}
