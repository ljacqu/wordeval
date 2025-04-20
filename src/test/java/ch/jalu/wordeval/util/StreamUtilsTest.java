package ch.jalu.wordeval.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Test for {@link StreamUtils}.
 */
class StreamUtilsTest {

  @Test
  void shouldKeepValuesDistinct() {
    // given
    List<String> values = List.of("one", "two", "three", "four", "five", "six", "seven");

    // when
    List<String> filteredValues = values.stream()
        .filter(StreamUtils.distinctByKey(String::length))
        .toList();

    // then
    assertThat(filteredValues, contains("one", "three", "four"));
  }
}