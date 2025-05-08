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
 * Test for {@link NoLineProcessor}.
 */
class NoLineProcessorTest extends AbstractLineProcessorTest {

  private final NoLineProcessor noLineProcessor = new NoLineProcessor();

  @Test
  void shouldSplitAndSanitizeWords_nynorsk() {
    HunspellDictionary nnDictionary = getDictionary("nn");
    assumeDictionaryFileExists(nnDictionary);

    // given / when
    Set<String> words = dictionaryService.readAllWords(nnDictionary).stream()
        .map(Word::getRaw)
        .collect(Collectors.toSet());

    // then
    assertThat(words, largeCollectionHasItems(
        "ørestaven",     // ørestav/A
        "ørestavar",     // ørestav/D
        "brislingnøter", // brislingnot/M
        "ufarleggjér",   // ufarleggjere/R
        "uvêr",
        "uvêra"  // uvêr/C
    ));
    assertThat(words, largeCollectionHasItems("vadmål", "vadmåla", "Danmark", "høgvass"));
    assertThat(words, largeCollectionHasNoneItems("univ.stip.", "v.l."));
  }

  @Test
  void shouldSplitAndSanitizeWords_bokmal() {
    HunspellDictionary nnDictionary = getDictionary("nb");
    assumeDictionaryFileExists(nnDictionary);

    // given / when
    Set<String> words = dictionaryService.readAllWords(nnDictionary).stream()
        .map(Word::getRaw)
        .collect(Collectors.toSet());

    // then
    assertThat(words, largeCollectionHasItems(
        "akterstavn",
        "akterstavnen", // akterstavn/A
        "liinger",      // liing/E
        "dagsøkta",     // dagsøkt/C
        "eplegeleene"   // eplegelé/G
    ));
    assertThat(words, largeCollectionHasItems("kilopondene", "anså", "antall", "dagtid"));
    assertThat(words, largeCollectionHasNoneItems("laud.", "lign."));
  }

  @Test
  void shouldSplitAndSanitize() {
    // given
    List<String> lines = List.of("agitasjon/ADFJ\\", "akantus/ADF", "k.s.", "kabyssor", "øsne/CEGK",
        "dagtid/\\", "dagtider/");

    // when
    List<String> result = processLines(lines, noLineProcessor);

    // then
    assertThat(result, contains("agitasjon", "akantus", "kabyssor", "øsne", "dagtid", "dagtider"));
  }
}
