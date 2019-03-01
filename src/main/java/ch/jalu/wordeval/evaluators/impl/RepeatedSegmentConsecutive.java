package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluatedWord;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import com.google.common.collect.ImmutableMultimap;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds words with repeating, consecutive sequences,
 * such as "elijk" in nl. "gelijkelijk".
 */
public class RepeatedSegmentConsecutive implements PostEvaluator {

  private static final Pattern REPETITION_AT_START = Pattern.compile("^(.{2,})(\\1+)");

  @Override
  public void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore resultStore) {
    ImmutableMultimap<Double, EvaluatedWord> repeatedSegmentResults =
      resultsProvider.getResultsOfEvaluatorOfType(ch.jalu.wordeval.evaluators.impl.RepeatedSegment.class);
    repeatedSegmentResults.values()
      .forEach(result -> processWord(result.getWord(), resultStore));
  }

  private void processWord(Word wordObject, ResultStore resultStore) {
    String word = wordObject.getLowercase();
    Map<String, String> results = new HashMap<>();
    for (int i = 0; i < word.length() - 2; ++i) {
      Matcher matcher = REPETITION_AT_START.matcher(word.substring(i));
      if (matcher.find()) {
        String segment = matcher.group(1);
        String repetition = segment + matcher.group(2);
        addResult(results, segment, repetition);
      }
    }
    results.values().forEach(v -> resultStore.addResult(wordObject, new EvaluationResult(v.length(), v)));
  }

  private static void addResult(Map<String, String> results, String segment, String repetition) {
    String storedRepetition = results.get(segment);
    if (storedRepetition == null || storedRepetition.length() < repetition.length()) {
      results.put(segment, repetition);
    }
  }
}
