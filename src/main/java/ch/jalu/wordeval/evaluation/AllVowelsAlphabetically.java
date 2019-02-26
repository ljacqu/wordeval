package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.evaluation.export.PartWordExport;
import ch.jalu.wordeval.evaluation.export.PartWordReducer;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Finds words with all vowels that appear alphabetically, such as "arbeidsonrust".
 */
public class AllVowelsAlphabetically extends PostEvaluator<String, VowelCount> {

  private final List<String> vowels;

  public AllVowelsAlphabetically(Language language) {
    vowels = language.getVowels();
  }

  /**
   * Post evaluator method to collect words with the most vowels or consonants.
   *
   * @param counter the vowel counter to base results on
   */
  @Override
  public void evaluateWith(VowelCount counter) {
    Multimap<String, String> results = counter.getResults();
    for (Map.Entry<String, String> entry : results.entries()) {
      if (hasVowelsAlphabetically(entry.getValue())) {
        addEntry(entry.getKey(), entry.getValue());
      }
    }
  }

  private boolean hasVowelsAlphabetically(String word) {
    int idx = 0;
    String curVowel = vowels.get(idx);
    for (int i = 0; i < word.length(); ++i) {
      String str = word.substring(i, i + 1);
      if (vowels.contains(str)) {
        if (str.equals(curVowel)) {
          idx++;
          curVowel = idx >= vowels.size() ? null : vowels.get(idx);
        } else {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public ExportObject toExportObject() {
    return toExportObject("AlphAllVowels",
        ExportParams.builder()
            .topKeys(4)
            .maxTopEntrySize(Optional.of(10))
            .maxPartWordListSize(Optional.of(10))
            .numberOfDetailedAggregation(Optional.of(0))
            .build());
  }

  /**
   * Base matcher method.
   *
   * @param counter the instance to investigate
   * @return base match result
   */
  @Override
  public boolean isMatch(VowelCount counter) {
    return LetterType.VOWELS == counter.getLetterType();
  }

  @Override public Class<VowelCount> getType() { return VowelCount.class; }

  @Override
  protected ExportObject toExportObject(String identifier, ExportParams params) {
    return PartWordExport.create(identifier, getResults(), params, new PartWordReducer.ByLength());
  }

  // TODO #50: Set export params to prefer short words with all vowels

}
