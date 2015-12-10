package ch.ljacqu.wordeval.evaluation;

import static ch.ljacqu.wordeval.TestUtil.asSet;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;

import ch.ljacqu.wordeval.language.LetterType;

public class AllVowelsTest {

  @Test
  public void shouldFindWordsWithAllVowels() {
    VowelCount counter = Mockito.mock(VowelCount.class);
    when(counter.getResults()).thenReturn(initializeSampleResults(LetterType.VOWELS));
    AllVowels evaluator = new AllVowels(LetterType.VOWELS);
    
    evaluator.postEvaluate(counter);
    
    Map<String, Set<String>> results = evaluator.getResults();
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
  
  private static Map<String, Set<String>> initializeSampleResults(LetterType letterType) {
    if (LetterType.VOWELS.equals(letterType)) {
      Map<String, Set<String>> results = new HashMap<>();
      results.put("ae", asSet("bear", "care"));
      results.put("aeiu", asSet("beautiful"));
      results.put("eiou", asSet("question", "questions", "questioning"));
      results.put("aeiou", asSet("sequoia", "miscellaneous", "simultaneous"));
      return results;
    } else if (LetterType.CONSONANTS.equals(letterType)) {
      Map<String, Set<String>> results = new HashMap<>();
      results.put("", asSet("a", "I"));
      results.put("tq", asSet("quite"));
      results.put("hrst", asSet("shirt", "shirts", "short"));
      results.put("ckrt", asSet("trick", "tricky"));
      return results;
    } else {
      throw new IllegalStateException("Given letter type is not supported");
    }
  }

}
