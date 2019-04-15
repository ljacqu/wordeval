package ch.jalu.wordeval.evaluators.result;

import ch.jalu.wordeval.dictionary.Word;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Getter
@RequiredArgsConstructor
public class WordGroup implements EvaluationResult {

  private final Set<Word> words;

  public WordGroup(Word... words) {
    this(newHashSet(words));
  }

}
