package ch.ljacqu.wordeval.evaluation.export;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import java.util.Optional;

import lombok.Builder;

/**
 * The export parameters define how the results of an evaluator should be preserved
 * and summarized to generate an export object.                                                                                                                                   
 */
public class ExportParams {

  /** The number of biggest keys to keep in full form. */
  public final int topKeys; // done
  
  /** Top entry minimum - any key below this minimum will not be added as top entry. */
  public final Optional<Double> topEntryMinimum; // done
  
  /** General minimum - any keys below this minimum will be discarded entirely. */
  public final Optional<Double> generalMinimum; // done
  
  /** If true, the first-level keys are in descending order. */
  public final boolean isDescending; // done
  
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
  ExportParams(Integer topKeys, Optional<Double> topEntryMinimum, Optional<Double> generalMinimum, 
      Boolean isDescending, Boolean hasDescendingEntries, Optional<Integer> maxTopEntrySize, 
      Optional<Integer> maxPartWordListSize, Optional<Integer> numberOfDetailedAggregation) { // NOSONAR
    this.topKeys = firstNonNull(topKeys, 5);
    this.topEntryMinimum = firstNonNull(topEntryMinimum, empty());
    this.generalMinimum = firstNonNull(generalMinimum, empty());
    this.isDescending = firstNonNull(isDescending, true);
    this.hasDescendingEntries = firstNonNull(hasDescendingEntries, false);
    this.maxTopEntrySize = firstNonNull(maxTopEntrySize, Optional.of(50));
    this.maxPartWordListSize = firstNonNull(maxPartWordListSize, emptyInt());
    this.numberOfDetailedAggregation = firstNonNull(numberOfDetailedAggregation, Optional.of(3));
  }
  
  // TODO #63: mvn verify throws errors if Optional.empty() is used directly
  private static Optional<Double> empty() {
    return Optional.empty();
  }
  
  private static Optional<Integer> emptyInt() {
    return Optional.empty();
  }

}
