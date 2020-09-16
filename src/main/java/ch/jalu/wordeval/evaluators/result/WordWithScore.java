package ch.jalu.wordeval.evaluators.result;

import ch.jalu.wordeval.dictionary.Word;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WordWithScore implements EvaluationResult {

  private final Word word;
  private final double score;

}
