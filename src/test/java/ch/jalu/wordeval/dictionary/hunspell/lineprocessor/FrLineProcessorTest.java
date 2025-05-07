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
 * Test for {@link FrLineProcessor}.
 */
class FrLineProcessorTest extends AbstractLineProcessorTest {

  private final FrLineProcessor frLineProcessor = new FrLineProcessor();

  @Test
  void shouldFindTheGivenWords() {
    HunspellDictionary frDictionary = getDictionary("fr");
    assumeDictionaryFileExists(frDictionary);

    // given / when
    Set<String> words = dictionaryService.readAllWords(frDictionary).stream()
      .map(Word::getRaw)
      .collect(Collectors.toSet());

    // then
    assertThat(words, largeCollectionHasItems("civil", "cil", "six")); // kind of looks like roman numerals...
    assertThat(words, largeCollectionHasItems("notre", "ou", "parmi", "te")); // from lines that don't have a / delimiter
    assertThat(words, largeCollectionHasItems("y", "zaouïa"));
    assertThat(words, largeCollectionHasItems("socratiser", "démurer", "boire")); // has digits after the delimiter

    assertThat(words, largeCollectionHasNoneItems("II", "III", "IIIe", "IIIes", "IIIᵉ", "IIIᵉˢ", "IV", "IVe", "IVᵉ", "IXes"));
    assertThat(words, largeCollectionHasNoneItems("brrr", "grrr", "pfft"));
    assertThat(words, largeCollectionHasNoneItems("-"));
  }

  @Test
  void shouldSplitAndSanitize() {
    // given
    List<String> lines = getLines();

    // when
    List<String> words = processLines(lines, frLineProcessor);

    // then
    assertThat(words, contains("able", "bin", "cat", "dog", "emu", "frog-fish-ferret", "gator", "joker"));
  }

  @Test
  void shouldStripAfterSpace() {
    // given
    List<String> lines = List.of("ape", "bat/A0 test", "cat cats", "dolphin/D. f", "ex. ", "frog./D0 test");

    // when
    List<String> words = processLines(lines, frLineProcessor);

    // then
    assertThat(words, contains("ape", "bat", "cat", "dolphin"));
  }

  private static List<String> getLines() {
    return List.of(
        "able/ApCo",
        "bin/D4P4 po:test",
        "cat",
        "dog po:an",
        "XIXe",
        "- po:ponc po:sign",
        "emu/XIXX",
        "frog-fish-ferret/P3 po:an",
        "pfft",
        "grrr",
        "XXXIIes",
        "gator/ee",
        "H₆Pl₉",
        "iffᵉ",
        "joker/e0",
        "Δt",
        "some",
        "thing",
        "else"
    );
  }
}
