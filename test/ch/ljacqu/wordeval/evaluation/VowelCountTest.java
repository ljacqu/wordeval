package ch.ljacqu.wordeval.evaluation;

import ch.ljacqu.wordeval.TestUtil;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LetterType;
import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;

import static ch.ljacqu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@SuppressWarnings("javadoc")
public class VowelCountTest {

  private VowelCount vowelEvaluator;
  private VowelCount consonantEvaluator;

  @Before
  public void initializeEvaluator() {
    Language lang = newLanguage("zxx");
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

    Multimap<String, String> vowelResults = vowelEvaluator.getResults();
    Multimap<String, String> consonantResults = consonantEvaluator.getResults();

    assertThat(vowelResults.keySet(), hasSize(8));
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
