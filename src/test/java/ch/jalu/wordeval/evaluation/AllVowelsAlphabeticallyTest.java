package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.language.Language;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link AllVowelsAlphabetically}.
 */
public class AllVowelsAlphabeticallyTest {

  @Test
  public void shouldFindMatches() {
    // given
    Language lang = newLanguage("zxx").build();
    AllVowelsAlphabetically allVowelsAlphabetically = new AllVowelsAlphabetically(lang);
    VowelCount vowelCounter = mock(VowelCount.class);
    given(vowelCounter.getResults()).willReturn(createVowelCountResults());

    // when
    allVowelsAlphabetically.evaluateWith(vowelCounter);

    // then
    Map<String, Collection<String>> results = allVowelsAlphabetically.getResults().asMap();
    assertThat(results, aMapWithSize(2));
    assertThat(results.get("aeiou"), containsInAnyOrder("arbeidsonrust", "marketingproduct"));
    assertThat(results.get("aei"), contains("aei"));
  }

  private static Multimap<String, String> createVowelCountResults() {
    Multimap<String, String> results = HashMultimap.create();
    results.put("aeiou", "arbeidsonrust");
    results.put("aei", "aei");
    results.put("aeiou", "aarbeidsonrust");
    results.put("e", "test");
    results.put("aeiou", "arbiedsonrust");
    results.put("aeiou", "marketingproduct");
    return results;
  }
}