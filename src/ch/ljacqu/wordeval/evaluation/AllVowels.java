package ch.ljacqu.wordeval.evaluation;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;
import ch.ljacqu.wordeval.language.LetterType;
import lombok.AllArgsConstructor;

/**
 * Evaluator that collects words with the most different vowels or consonants.
 */
@AllArgsConstructor
public class AllVowels extends PartWordEvaluator implements PostEvaluator<VowelCount> {

  private final LetterType letterType;
  
  @Override
  public void processWord(String word, String rawWord) {
    // --
  }

  /**
   * Post evaluator method to collect words with the most vowels or consonants.
   * @param counter the vowel counter to base results on
   */
  @Override
  public void evaluateWith(VowelCount counter) {
    Map<String, Set<String>> results = counter.getResults();
    // TODO: This and SingleVowel actually just need the results from VowelCount and different reducers / export params
    results.entrySet().stream()
      .forEach(entry -> getResults().put(entry.getKey(), entry.getValue()));
  }

  public ExportObject toExportObject() {
    return toExportObject("AllVowels_" + letterType.getName(),
      ExportParams.builder()
        .topKeys(4)
        .maxTopEntrySize(Optional.of(10))
        .maxPartWordListSize(Optional.of(10))
        .numberOfDetailedAggregation(Optional.of(0))
        .build());
  }
  
  /**
   * Base matcher method.
   * @param counter the instance to investigate
   * @return base match result
   */
  @Override
  public boolean isMatch(VowelCount counter) {
    return letterType.equals(counter.getLetterType());
  }

  @Override public Class<VowelCount> getType() { return VowelCount.class; }
  
  // TODO #50: Set export params to prefer short words with all vowels

}
