package ch.ljacqu.wordeval.evaluation;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;
import ch.ljacqu.wordeval.language.LetterType;
import lombok.AllArgsConstructor;

/**
 * Evaluator collecting words which only have one distinct vowel or consonant,
 * such as "abracadabra," which only uses the vowel 'a.' 
 */
@AllArgsConstructor
public class SingleVowel extends PartWordEvaluator implements PostEvaluator<VowelCount> {
  
  private final LetterType letterType;
  
  @Override
  public void processWord(String word, String rawWord) {
    // --
  }

  /**
   * Isolates the words with one letter type from a {@link VowelCount} instance.
   * @param counter the vowel or consonant counter
   */
  @Override
  public void evaluateWith(VowelCount counter) {
    Map<String, Set<String>> results = counter.getResults();
    Integer min = results.keySet().stream()
      .map(String::length)
      .min(Integer::compare)
      .orElse(null);
    if (min == null) {
      throw new IllegalStateException("Could not get minimum - no words with letter type?");
    } else if (min == 0) {
      min = 1;
    }

    // TODO: This just needs a proper reducer / params and we can just take the results of VowelCount
    for (Map.Entry<String, Set<String>> entry : results.entrySet()) {
      if (entry.getKey().length() <= min) {
        getResults().put(entry.getKey(), entry.getValue());
      }
    }
  }

  /**
   * Base matcher to match the correct VowelCount instance to this evaluator.
   * @param counter the instance to match
   * @return base match
   */
  @Override
  public boolean isMatch(VowelCount counter) {
    return letterType.equals(counter.getLetterType());
  }
  
  @Override
  public ExportObject toExportObject() {
    // TODO #50: Prioritize words with higher length
    return toExportObject(this.getClass().getSimpleName() + "_" + letterType.getName(),
        ExportParams.builder()
            .maxTopEntrySize(Optional.of(10))
            .maxPartWordListSize(Optional.of(10))
            .build());
  }

  @Override public Class<VowelCount> getType() { return VowelCount.class; }

}
