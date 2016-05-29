package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.TestUtil;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link RepeatedSegment}.
 */
public class RepeatedSegmentTest {

  @Test
  public void shouldFindMatch() {
    // given
    // 3x est; 2x an; 2x ssi, 2x iss; 2x an, 2x na; -
    String[] words = {"geestestoestand", "banane", "mississippi", "ananas", "something"};
    RepeatedSegment evaluator = new RepeatedSegment();

    // when
    TestUtil.processWords(evaluator, words);

    // then
    Map<String, Collection<String>> results = evaluator.getResults().asMap();
    assertThat(results, aMapWithSize(5));
    assertThat(results.get("3,est"), contains("geestestoestand"));
    assertThat(results.get("2,an"), containsInAnyOrder("banane", "ananas"));
    assertThat(results.get("2,ssi"), contains("mississippi"));
    assertThat(results.get("2,iss"), contains("mississippi"));
    assertThat(results.get("2,na"), contains("ananas"));
  }

}