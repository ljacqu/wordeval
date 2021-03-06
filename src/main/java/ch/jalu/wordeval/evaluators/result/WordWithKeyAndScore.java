package ch.jalu.wordeval.evaluators.result;

import ch.jalu.wordeval.dictionary.Word;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WordWithKeyAndScore implements EvaluationResult {

  private final Word word;
  private final String key;
  private final int score;
}
