package ch.ljacqu.wordeval.evaluation;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import ch.ljacqu.wordeval.language.Alphabet;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LetterType;

public class MonotoneVowelTest {

  private MonotoneVowel vowelEvaluator;
  private MonotoneVowel consonantEvaluator;

  @Before
  public void initializeEvaluator() {
    Language lang = new Language("zxx", Alphabet.LATIN);
    vowelEvaluator = new MonotoneVowel(LetterType.VOWELS, lang);
    consonantEvaluator = new MonotoneVowel(LetterType.CONSONANTS, lang);
  }

  private void process(String[] words) {
    for (String word : words) {
      vowelEvaluator.processWord(word, word);
      consonantEvaluator.processWord(word, word);
    }
  }

  @Test
  public void shouldProcessWordsCorrectly() {
    // lengths are 10, 9, 5, 5, 10, 8, 8, 0
    String[] words = { "assosiasie", "something", "flute", "geeet",
        "madagascar", "tatotute", "cocacoci", "eau" };
    process(words);

    Map<Integer, List<String>> vowelResults = vowelEvaluator
        .getNavigableResults();
    Map<Integer, List<String>> consonantResults = consonantEvaluator
        .getNavigableResults();

    assertThat(vowelResults, aMapWithSize(2));
    assertThat(vowelResults.get(10), contains("madagascar"));
    assertThat(vowelResults.get(5), contains("geeet"));

    assertThat(consonantResults, aMapWithSize(2));
    assertThat(consonantResults.get(10), contains("assosiasie"));
    assertThat(consonantResults.get(8),
        containsInAnyOrder("tatotute", "cocacoci"));
  }

}
