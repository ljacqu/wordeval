package ch.jalu.wordeval.evaluation;

import com.google.common.collect.ImmutableMultimap;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link RepeatedSegmentConsecutive}.
 */
public class RepeatedSegmentConsecutiveTest {

  @Test
  public void shouldAddResults() {
    // given
    RepeatedSegment base = mock(RepeatedSegment.class);
    ImmutableMultimap<String, String> baseResults =
        // keys are irrelevant for the postevaluator
      ImmutableMultimap.<String, String>builder()
        .put("a", "gelijkelijk") // elijkelijk
        .put("b", "banana")      // anan, nana
        .put("c", "nothing")     // --
        .put("d", "rentrent")    // rentrent
        .put("e", "Wanderer")    // erer
        .put("f", "bebendo")     // bebe
        .put("g", "lalaBlalala") // lalala
        .put("h", "barbarian")   // barbar
        .put("h", "barbarians")  // barbar
        .build();
    given(base.getResults()).willReturn(baseResults);
    RepeatedSegmentConsecutive evaluator = new RepeatedSegmentConsecutive();

    // when
    evaluator.evaluateWith(base);

    // then
    Map<String, Collection<String>> results = evaluator.getResults().asMap();
    assertThat(results, aMapWithSize(9));
    assertThat(results.get("elijkelijk"), contains("gelijkelijk"));
    assertThat(results.get("anan"), contains("banana"));
    assertThat(results.get("nana"), contains("banana"));
    assertThat(results.get("rentrent"), contains("rentrent"));
    assertThat(results.get("erer"), contains("Wanderer"));
    assertThat(results.get("bebe"), contains("bebendo"));
    assertThat(results.get("lalala"), contains("lalaBlalala"));
    assertThat(results, not(hasKey("lala")));
    assertThat(results.get("barbar"), containsInAnyOrder("barbarian", "barbarians"));
  }

}