package ch.ljacqu.wordeval.evaluation;

import ch.ljacqu.wordeval.language.LetterType;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.junit.Test;
import org.mockito.Mockito;

import static ch.ljacqu.wordeval.TestUtil.asSet;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class AllVowelsTest {

  @Test
  public void shouldFindWordsWithAllVowels() {
    VowelCount counter = Mockito.mock(VowelCount.class);
    when(counter.getResults()).thenReturn(initializeSampleResults(LetterType.VOWELS));
    AllVowels evaluator = new AllVowels(LetterType.VOWELS);
    
    evaluator.evaluateWith(counter);

    Multimap<String, String> results = evaluator.getResults();
    assertThat(results.keySet(), containsInAnyOrder("aeiou", "eiou", "aeiu", "ae"));
    assertThat(results.get("aeiou"), containsInAnyOrder("sequoia", "miscellaneous", "simultaneous"));
    assertThat(results.get("eiou"), containsInAnyOrder("question", "questions", "questioning"));
  }
  
  @Test
  public void shouldMatchLetterType() {
    VowelCount counter1 = Mockito.mock(VowelCount.class);
    when(counter1.getLetterType()).thenReturn(LetterType.CONSONANTS);
    AllVowels allVowels1 = new AllVowels(LetterType.CONSONANTS);
    
    VowelCount counter2 = Mockito.mock(VowelCount.class);
    when(counter2.getLetterType()).thenReturn(LetterType.VOWELS);
    AllVowels allVowels2 = new AllVowels(LetterType.VOWELS);
    
    assertThat(allVowels1.isBaseMatch(counter1), equalTo(Boolean.TRUE));
    assertThat(allVowels2.isBaseMatch(counter2), equalTo(Boolean.TRUE));
    assertThat(allVowels1.isBaseMatch(counter2), equalTo(Boolean.FALSE));
    assertThat(allVowels2.isBaseMatch(counter1), equalTo(Boolean.FALSE));
  }
  
  @Test
  public void shouldHaveEmptyProcessWordMethod() {
    AllVowels allVowels = new AllVowels(LetterType.CONSONANTS);
    
    allVowels.processWord("word", "word");
    // Nothing happens
  }
  
  private static TreeMultimap<String, String> initializeSampleResults(LetterType letterType) {
    TreeMultimap<String, String> results = TreeMultimap.create();
    if (LetterType.VOWELS.equals(letterType)) {
      results.putAll("ae", asSet("bear", "care"));
      results.putAll("aeiu", asSet("beautiful"));
      results.putAll("eiou", asSet("question", "questions", "questioning"));
      results.putAll("aeiou", asSet("sequoia", "miscellaneous", "simultaneous"));
    } else if (LetterType.CONSONANTS.equals(letterType)) {
      results.putAll("", asSet("a", "I"));
      results.putAll("tq", asSet("quite"));
      results.putAll("hrst", asSet("shirt", "shirts", "short"));
      results.putAll("ckrt", asSet("trick", "tricky"));
    } else {
      throw new IllegalStateException("Given letter type is not supported");
    }
    return results;
  }

}
