package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.language.Alphabet;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Test for {@link DiacriticHomonyms}.
 */
class DiacriticHomonymsTest extends AbstractEvaluatorTest {

  private final DiacriticHomonyms diacriticHomonyms = new DiacriticHomonyms();

  @Test
  void shouldFindDiacriticHomonyms() {
    // given
    List<Word> words = Stream.of("schön", "schon", "sûr", "sur", "ça", "çà", "des", "dés", "dès", "le", "la", "là")
      .map(TestWord::new)
      .peek(w -> w.setWithoutAccents(Alphabet.LATIN.removeAccents(w.getLowercase())))
      .collect(Collectors.toList());

    // when
    diacriticHomonyms.evaluate(words);

    // then
    Map<String, Set<String>> results = groupResultsByKey(diacriticHomonyms.getResults());
    assertThat(results.keySet(), containsInAnyOrder("schon", "sur", "ca", "des", "la"));
    assertThat(results.get("sur"), containsInAnyOrder("sur", "sûr"));
    assertThat(results.get("ca"), containsInAnyOrder("ça", "çà"));
    assertThat(results.get("schon"), containsInAnyOrder("schon", "schön"));
  }
}
