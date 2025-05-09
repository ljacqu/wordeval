package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.export.EvaluatorExportUtil;
import ch.jalu.wordeval.evaluators.processing.EvaluatorCollection;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithScore;
import com.google.common.collect.ListMultimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Evaluator that finds proper palindromes based on the results of the
 * {@link Palindromes} evaluator, which also matches parts of a word (e.g.
 * "ette" in "better").
 */
public class FullPalindromes implements PostEvaluator {

  @Getter
  private final List<WordWithScore> results = new ArrayList<>();

  @Override
  public void evaluate(EvaluatorCollection evaluators) {
    List<WordWithKey> palindromeResults = evaluators.getWordEvaluatorOrThrow(Palindromes.class).getResults();

    for (WordWithKey entry : palindromeResults) {
      Word word = entry.word();
      int wordLength = word.getWithoutAccentsWordCharsOnly().length();
      if (wordLength == entry.key().length()) {
        results.add(new WordWithScore(word, wordLength));
      }
    }
  }

  @Override
  public String getId() {
    return "palindromes.full";
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
}
