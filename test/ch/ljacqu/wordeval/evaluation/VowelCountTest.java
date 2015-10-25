package ch.ljacqu.wordeval.evaluation;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ch.ljacqu.wordeval.TestUtil;
import ch.ljacqu.wordeval.language.Alphabet;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LetterType;

public class VowelCountTest {

  private VowelCount vowelEvaluator;
  private VowelCount consonantEvaluator;

  @Before
  public void initializeEvaluator() {
    Language lang = new Language("zxx", Alphabet.LATIN);
    vowelEvaluator = new VowelCount(LetterType.VOWELS, lang);
    consonantEvaluator = new VowelCount(LetterType.CONSONANTS, lang);
  }

  private void process(String[] words) {
    TestUtil.processWords(vowelEvaluator, words);
    TestUtil.processWords(consonantEvaluator, words);
  }

  @Test
  public void shouldProcessWordsCorrectly() {
    // vowels: aeio, eio, eu, e, a, aou, aio, aeu, e
    // consonants: s, ghnst, flt, gt, cdgmrs, t, c, -, flt
    String[] words = { "assosiasie", "something", "flute", "geeet",
        "madagascar", "tatotute", "cocacoci", "eau", "fleet" };
    process(words);

    Map<String, Set<String>> vowelResults = vowelEvaluator.getResults();
    Map<String, Set<String>> consonantResults = consonantEvaluator.getResults();

    assertThat(vowelResults, aMapWithSize(8));
    assertThat(vowelResults.get("a"), contains("madagascar"));
    assertThat(vowelResults.get("e"), containsInAnyOrder("geeet", "fleet"));
    assertThat(vowelResults.get("eio"), contains("something"));
    assertThat(vowelResults.get("aeio"), contains("assosiasie"));

    assertThat(consonantResults, aMapWithSize(8));
    assertThat(consonantResults.get("flt"), containsInAnyOrder("flute", "fleet"));
    assertThat(consonantResults.get("cdgmrs"), containsInAnyOrder("madagascar"));
    assertThat(consonantResults.get("s"), contains("assosiasie"));
    assertThat(consonantResults.get("t"), contains("tatotute"));
    assertThat(consonantResults.get(""), contains("eau"));
  }

}
