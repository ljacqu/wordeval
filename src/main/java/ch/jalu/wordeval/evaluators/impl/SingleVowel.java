package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.export.EvaluatorExportUtil;
import ch.jalu.wordeval.evaluators.processing.EvaluatorCollection;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithScore;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.ListMultimap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Evaluator collecting words which only have one distinct vowel or consonant,
 * such as "abracadabra," which only uses the vowel 'a'.
 */
@AllArgsConstructor
@ToString(of = "letterType")
public class SingleVowel implements PostEvaluator {

  @Getter
  private final LetterType letterType;
  private final List<WordWithScore> results = new ArrayList<>();

  @Override
  public void evaluate(EvaluatorCollection evaluators) {
    VowelCount vowelCountEvaluator = evaluators.getWordEvaluatorOrThrow(VowelCount.class,
        vowelCount -> vowelCount.getLetterType() == letterType);

    List<WordWithKey> vowelCountResults = vowelCountEvaluator.getResults();

    int min = vowelCountResults.stream()
        .mapToInt(e -> e.key().length())
        .filter(len -> len > 0)
        .min()
        .orElseThrow(() -> new IllegalStateException("Could not get minimum - no words with letter type?"));

    vowelCountResults.stream()
        .filter(e -> e.key().length() == min)
        .forEach(e -> this.results.add(new WordWithScore(e.word(), e.word().getLowercase().length())));
  }

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
    List<WordWithScore> sortedResult = results.stream()
        .sorted(Comparator.comparing(WordWithScore::score).reversed())
        .toList();

    Set<Double> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = EvaluatorExportUtil.newListMultimap();
    for (WordWithScore wordWithScore : sortedResult) {
      if (uniqueValues.add(wordWithScore.score()) && uniqueValues.size() > topScores) {
        break;
      }
      filteredResults.put((int) wordWithScore.score(), wordWithScore.word().getRaw());
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return filteredResults;
  }

  @Override
  public String getId() {
    return switch (letterType) {
      case VOWELS -> "vowels.single";
      case CONSONANTS -> "consonants.single";
    };
  }
}
