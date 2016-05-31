package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.evaluation.export.PartWordExport;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds words with repeating, consecutive sequences,
 * such as "elijk" in nl. "gelijkelijk".
 */
public class RepeatedSegmentConsecutive extends PostEvaluator<String, RepeatedSegment> {

  private static final Pattern REPETITION_AT_START = Pattern.compile("^(.{2,})(\\1+)");

  @Override
  public void evaluateWith(RepeatedSegment evaluator) {
    evaluator.getResults().values().forEach(this::processWord);
  }

  private void processWord(String word) {
    Map<String, String> results = new HashMap<>();
    for (int i = 0; i < word.length() - 2; ++i) {
      Matcher matcher = REPETITION_AT_START.matcher(word.substring(i));
      if (matcher.find()) {
        String segment = matcher.group(1);
        String repetition = segment + matcher.group(2);
        addResult(results, segment, repetition);
      }
    }
    results.values().forEach(v -> addEntry(v, word));
  }

  private static void addResult(Map<String, String> results, String segment, String repetition) {
    String storedRepetition = results.get(segment);
    if (storedRepetition == null || storedRepetition.length() < repetition.length()) {
      results.put(segment, repetition);
    }
  }

  @Override
  public Class<RepeatedSegment> getType() {
    return RepeatedSegment.class;
  }

  @Override
  protected ExportObject toExportObject(String identifier, ExportParams params) {
    return PartWordExport.create(getClass().getSimpleName(), getResults(), null, null);
  }
}
