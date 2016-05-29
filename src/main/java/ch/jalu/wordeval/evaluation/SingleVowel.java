package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.evaluation.export.PartWordExport;
import ch.jalu.wordeval.evaluation.export.PartWordReducer;
import ch.jalu.wordeval.language.LetterType;
import ch.jalu.wordeval.evaluation.export.ExportObject;
import com.google.common.collect.TreeMultimap;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * Evaluator collecting words which only have one distinct vowel or consonant,
 * such as "abracadabra," which only uses the vowel 'a.' 
 */
@AllArgsConstructor
public class SingleVowel extends PostEvaluator<String, VowelCount> {
  
  private final LetterType letterType;

  /**
   * Isolates the words with one letter type from a {@link VowelCount} instance.
   * @param counter the vowel or consonant counter
   */
  @Override
  public void evaluateWith(VowelCount counter) {
    TreeMultimap<String, String> results = counter.getResults();

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
    for (Map.Entry<String, String> entry : results.entries()) {
      if (entry.getKey().length() <= min) {
        getResults().put(entry.getKey(), entry.getValue());
      }
    }
  }

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

  @Override
  protected ExportObject toExportObject(String identifier, ExportParams params) {
    return PartWordExport.create(identifier, getResults(), params, new PartWordReducer.ByLength());
  }

}
