package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.export.EvaluatorExportUtil;
import ch.jalu.wordeval.evaluators.processing.AllWordsEvaluatorProvider;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithScore;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.ListMultimap;
import lombok.AllArgsConstructor;

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
public class SingleVowel implements PostEvaluator {

  private final LetterType letterType;
  private final List<WordWithScore> results = new ArrayList<>();

  @Override
  public void evaluate(AllWordsEvaluatorProvider allWordsEvaluatorProvider) {
    VowelCount vowelCountEvaluator = allWordsEvaluatorProvider.getEvaluator(VowelCount.class,
        vowelCount -> vowelCount.getLetterType() == letterType);

    List<WordWithKey> vowelCountResults = vowelCountEvaluator.getResults();

    int min = vowelCountResults.stream()
        .mapToInt(e -> e.getKey().length())
        .filter(len -> len > 0)
        .min()
        .orElseThrow(() -> new IllegalStateException("Could not get minimum - no words with letter type?"));

    vowelCountResults.stream()
        .filter(e -> e.getKey().length() == min)
        .forEach(e -> this.results.add(new WordWithScore(e.getWord(), e.getWord().getLowercase().length())));
  }

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
    List<WordWithScore> sortedResult = results.stream()
        .sorted(Comparator.comparing(WordWithScore::getScore).reversed())
        .toList();

    Set<Double> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = EvaluatorExportUtil.newListMultimap();
    for (WordWithScore wordWithScore : sortedResult) {
      if (uniqueValues.add(wordWithScore.getScore()) && uniqueValues.size() > topScores) {
        break;
      }
      filteredResults.put((int) wordWithScore.getScore(), wordWithScore.getWord().getRaw());
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return filteredResults;
  }

  @Override
  public String getId() {
    return switch (letterType) {
      case VOWELS -> "SingleVowel";
      case CONSONANTS -> "SingleConsonant";
    };
  }
}
