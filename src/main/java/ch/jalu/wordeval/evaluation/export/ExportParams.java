package ch.jalu.wordeval.evaluation.export;

import lombok.Builder;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

/**
 * The export parameters define how the results of an evaluator should be preserved
 * and summarized to generate an export object.                                                                                                                                   
 */
public class ExportParams {

  /** The number of biggest keys to keep in full form. */
  public final int topKeys;
  
  /** Top entry minimum - any key below this minimum will not be added as top entry. */
  public final Optional<Double> topEntryMinimum;
  
  /** General minimum - any keys below this minimum will be discarded entirely. */
  public final Optional<Double> generalMinimum;
  
  /** If true, the first-level keys are in descending order. */
  public final boolean isDescending;
  
  /** For PartWordExport, whether the second dimension keys should be descending. */
  public final boolean hasDescendingEntries;
  
  /** 
   * The maximum number of entries per key to keep. For PartWordExport, the number of
   * subkeys to retain per first-level key. 
   */
  public final Optional<Integer> maxTopEntrySize;
  
  /** For PartWordExport, the maximum size a word list may have. */
  public final Optional<Integer> maxPartWordListSize;
  
  /** 
   * For PartWordExport, number of keys to aggregate in a detailed form before just
   * summarizing a key with its total results.
   */
  public final Optional<Integer> numberOfDetailedAggregation;

  @Builder
  @SuppressWarnings("unchecked")
  ExportParams(Integer topKeys, Optional<Double> topEntryMinimum, Optional<Double> generalMinimum, // NOSONAR
               Boolean isDescending, Boolean hasDescendingEntries, Optional<Integer> maxTopEntrySize,
               Optional<Integer> maxPartWordListSize, Optional<Integer> numberOfDetailedAggregation) {
    this.topKeys = firstNonNull(topKeys, 5);
    this.topEntryMinimum = firstNonNull(topEntryMinimum, Optional.<Double>empty());
    this.generalMinimum = firstNonNull(generalMinimum, Optional.<Double>empty());
    this.isDescending = firstNonNull(isDescending, true);
    this.hasDescendingEntries = firstNonNull(hasDescendingEntries, false);
    this.maxTopEntrySize = firstNonNull(maxTopEntrySize, Optional.of(50));
    this.maxPartWordListSize = firstNonNull(maxPartWordListSize, Optional.<Integer>empty());
    this.numberOfDetailedAggregation = firstNonNull(numberOfDetailedAggregation, Optional.of(3));
  }

}
