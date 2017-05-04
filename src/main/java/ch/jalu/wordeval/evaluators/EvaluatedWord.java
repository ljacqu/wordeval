package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.dictionary.WordForm;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;

/**
 * Pairs a word with a result.
 */
@Getter
@AllArgsConstructor
public class EvaluatedWord<R extends Comparable<R>> implements Comparable<EvaluatedWord<R>> {

  private static final Comparator<EvaluatedWord<?>> COMPARATOR = buildComparator();

  private final Word word;
  private final EvaluationResult<R> result;

  @Override
  public int compareTo(EvaluatedWord<R> o) {
    return COMPARATOR.compare(this, o);
  }

  private static Comparator<EvaluatedWord<?>> buildComparator() {
    Comparator<EvaluatedWord<?>> scoreComparator = Comparator.comparing(ew -> ew.getResult().getScore());
    Comparator<EvaluatedWord<?>> wordComparator = Comparator.comparing(ew -> ew.getWord().getForm(WordForm.RAW));
    return scoreComparator.thenComparing(wordComparator);
  }
}
