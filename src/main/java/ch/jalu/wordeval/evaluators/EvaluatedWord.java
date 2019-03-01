package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.Word;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

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
    Comparator<EvaluatedWord> scoreComparator =
      Comparator.comparing(ew -> getResultScore(ew.getResult()), nullsLast(naturalOrder()));
    Comparator<EvaluatedWord> wordComparator =
      Comparator.comparing(ew -> getRawForm(ew.getWord()), nullsLast(naturalOrder()));
    return scoreComparator.thenComparing(wordComparator);
  }

  private static Double getResultScore(EvaluationResult result) {
    return result == null ? null : result.getScore();
  }

  private static String getRawForm(Word word) {
    return word == null ? null : word.getRaw();
  }
}
