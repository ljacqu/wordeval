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
public class EvaluatedWord implements Comparable<EvaluatedWord> {

  private static final Comparator<EvaluatedWord> COMPARATOR = buildComparator();

  private final Word word;
  private final EvaluationResult result;

  @Override
  public int compareTo(EvaluatedWord o) {
    return COMPARATOR.compare(this, o);
  }

  private static Comparator<EvaluatedWord> buildComparator() {
    Comparator<EvaluatedWord> scoreComparator = Comparator.comparing(ew -> ew.getResult().getScore());
    Comparator<EvaluatedWord> wordComparator = Comparator.comparing(ew -> ew.getWord().getForm(WordForm.RAW));
    return scoreComparator.thenComparing(wordComparator);
  }
}
