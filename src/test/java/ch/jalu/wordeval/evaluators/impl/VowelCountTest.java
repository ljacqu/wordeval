package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

/**
 * Test for {@link VowelCount}.
 */
class VowelCountTest extends AbstractEvaluatorTest {

  private VowelCount vowelEvaluator;
  private VowelCount consonantEvaluator;

  @BeforeEach
  void initializeEvaluators() {
    Language lang = TestUtil.newLanguage("zxx").build();
    vowelEvaluator = new VowelCount(lang, LetterType.VOWELS);
    consonantEvaluator = new VowelCount(lang, LetterType.CONSONANTS);
  }

  @Test
  void shouldProcessWordsCorrectly() {
    // given
    // vowels: aeio, eio, eu, e, a, aou, aio, aeu, e
    // consonants: s, ghnst, flt, gt, cdgmrs, t, c, -, flt
    List<Word> words = createWords(
        "assosiasie", "something", "flute", "geeet", "madagascar", "tatotute", "cocacoci", "eau", "fleet");

    // when
    vowelEvaluator.evaluate(words);
    consonantEvaluator.evaluate(words);

    // then
    Map<String, Set<String>> vowelResults = groupByKey(vowelEvaluator.getResults());
    Map<String, Set<String>> consonantResults = groupByKey(consonantEvaluator.getResults());
    assertThat(vowelResults, aMapWithSize(8));
    assertThat(vowelResults.get("a"), contains("madagascar"));
    assertThat(vowelResults.get("e"), containsInAnyOrder("geeet", "fleet"));
    assertThat(vowelResults.get("eio"), contains("something"));
    assertThat(vowelResults.get("aeio"), contains("assosiasie"));

    assertThat(consonantResults.keySet(), hasSize(8));
    assertThat(consonantResults.get("flt"), containsInAnyOrder("flute", "fleet"));
    assertThat(consonantResults.get("cdgmrs"), containsInAnyOrder("madagascar"));
    assertThat(consonantResults.get("s"), contains("assosiasie"));
    assertThat(consonantResults.get("t"), contains("tatotute"));
    assertThat(consonantResults.get(""), contains("eau"));
  }
}
