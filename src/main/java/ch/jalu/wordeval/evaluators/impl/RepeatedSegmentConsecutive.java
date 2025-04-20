package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.export.EvaluatorExportUtil;
import ch.jalu.wordeval.evaluators.processing.EvaluatorCollection;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.util.StreamUtils;
import com.google.common.collect.ListMultimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds words with repeating, consecutive sequences,
 * such as "elijk" in nl. "gelijkelijk".
 */
public class RepeatedSegmentConsecutive implements PostEvaluator {

  private static final Pattern REPETITION_AT_START = Pattern.compile("^(.{2,})(\\1+)");

  @Getter
  private final List<WordWithKey> results = new ArrayList<>();

  @Override
  public void evaluate(EvaluatorCollection evaluators) {
    evaluators.getWordEvaluatorOrThrow(RepeatedSegment.class).getResults().stream()
        .filter(StreamUtils.distinctByKey(entry -> entry.getWord().getLowercase()))
        .forEach(result -> processWord(result.getWord()));
  }

  private void processWord(Word wordObject) {
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
    results.values().forEach(v -> this.results.add(new WordWithKey(wordObject, v)));
  }

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
    List<WordWithKey> sortedResult = results.stream()
        .sorted(Comparator.<WordWithKey>comparingInt(wordWithKey -> wordWithKey.getKey().length()).reversed())
        .toList();

    Set<Integer> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = EvaluatorExportUtil.newListMultimap();
    for (WordWithKey wordWithKey : sortedResult) {
      int score = wordWithKey.getKey().length();
      if (uniqueValues.add(score) && uniqueValues.size() > topScores) {
        break;
      }
      filteredResults.put(score, wordWithKey.getWord().getRaw() + " (" + wordWithKey.getKey() + ")");
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return filteredResults;
  }

  @Override
  public String getId() {
    return "repeatedSegment.consecutive";
  }

  private static void addResult(Map<String, String> results, String segment, String repetition) {
    String storedRepetition = results.get(segment);
    if (storedRepetition == null || storedRepetition.length() < repetition.length()) {
      results.put(segment, repetition);
    }
  }
}
