package ch.jalu.wordeval.dictionary;

import java.util.stream.IntStream;

/**
 * Word object for test purposes.
 */
public class TestWord extends Word {

  public TestWord(String word) {
    super(IntStream.range(0, WordForm.values().length)
        .mapToObj(i -> word)
        .toArray(String[]::new));
  }
}
