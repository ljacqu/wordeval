package ch.jalu.wordeval.evaluators.result;

import ch.jalu.wordeval.dictionary.Word;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class WordGroupWithKey implements EvaluationResult {

  private final Set<Word> words;
  private final String key;

}
