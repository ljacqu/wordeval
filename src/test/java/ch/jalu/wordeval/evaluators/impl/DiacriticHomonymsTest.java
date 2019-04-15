package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluatorTestHelper;
import ch.jalu.wordeval.language.Alphabet;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link DiacriticHomonyms}.
 */
public class DiacriticHomonymsTest {

  private DiacriticHomonyms evaluator = new DiacriticHomonyms();

  @Test
  public void shouldFindDiacriticHomonyms() {
    // given
    List<Word> words = Stream.of("schön", "schon", "sûr", "sur", "ça", "çà", "des", "dés", "dès", "le", "la", "là")
      .map(TestWord::new)
      .peek(w -> w.setWithoutAccents(Alphabet.LATIN.removeAccents(w.getLowercase())))
      .collect(Collectors.toList());

    // when
    Map<String, Set<String>> results = EvaluatorTestHelper.evaluateAndGroupWordsByKey(evaluator, words);

    // then
    assertThat(results.keySet(), containsInAnyOrder("schon", "sur", "ca", "des", "la"));
    assertThat(results.get("sur"), containsInAnyOrder("sur", "sûr"));
    assertThat(results.get("ca"), containsInAnyOrder("ça", "çà"));
    assertThat(results.get("schon"), containsInAnyOrder("schon", "schön"));
  }
}
