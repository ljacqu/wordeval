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
 * Test for {@link PtPtLineProcessor}.
 */
class PtPtLineProcessorTest extends AbstractLineProcessorTest {

  private final PtPtLineProcessor ptPtLineProcessor = new PtPtLineProcessor();

  @Test
  void shouldSplitAndSanitizeWords() {
    HunspellDictionary ptPtDictionary = getDictionary("pt-pt");
    assumeDictionaryFileExists(ptPtDictionary);

    // given / when
    Set<String> words = dictionaryService.readAllWords(ptPtDictionary).stream()
        .map(Word::getRaw)
        .collect(Collectors.toSet());

    // then
    assertThat(words, largeCollectionHasItems("fax", "faxes", "rap", "Trotski"));
    assertThat(words, largeCollectionHasItems("despejar", "despejamos", "despejamento"));
    assertThat(words, largeCollectionHasNoneItems("!", "("));
  }

  @Test
  void shouldSplitAndSanitize() {
    // given
    List<String> lines = List.of("!\t[CAT=punctg]", "abelha/p\t[CAT=nc,G=f,N=s]", "absorvente\t[CAT=a_nc,N=s,G=_]",
        "quimbundo  \t[CAT=nc,G=m,N=s]", "test", "etc\t[CAT=punct,ABR=1]");

    // when
    List<String> result = processLines(lines, ptPtLineProcessor);

    // then
    assertThat(result, contains("abelha", "absorvente", "quimbundo", "test", "etc"));
  }
}
