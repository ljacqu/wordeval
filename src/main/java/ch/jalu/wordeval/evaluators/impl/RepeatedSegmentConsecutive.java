package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithKeyAndScore;
import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds words with repeating, consecutive sequences,
 * such as "elijk" in nl. "gelijkelijk".
 */
public class RepeatedSegmentConsecutive implements PostEvaluator<WordWithKey> {

  private static final Pattern REPETITION_AT_START = Pattern.compile("^(.{2,})(\\1+)");

  @Override
  public void evaluateAndSaveResults(ResultsProvider resultsProvider, ResultStore<WordWithKey> resultStore) {
    ImmutableList<WordWithKeyAndScore> repeatedSegmentResults =
      resultsProvider.getResultsOfEvaluatorOfType(RepeatedSegment.class);
    repeatedSegmentResults.forEach(result -> processWord(result.getWord(), resultStore));
  }

  private void processWord(Word wordObject, ResultStore<WordWithKey> resultStore) {
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
    results.values().forEach(v -> resultStore.addResult(new WordWithKey(wordObject, v)));
  }

  private static void addResult(Map<String, String> results, String segment, String repetition) {
    String storedRepetition = results.get(segment);
    if (storedRepetition == null || storedRepetition.length() < repetition.length()) {
      results.put(segment, repetition);
    }
  }
}
