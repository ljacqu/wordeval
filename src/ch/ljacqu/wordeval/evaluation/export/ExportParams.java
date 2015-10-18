package ch.ljacqu.wordeval.evaluation.export;

import java.util.Optional;

import lombok.Builder;

/**
 * The export parameters, defining how the information of an evaluator's results
 * should be kept.
 */
public class ExportParams {

  /** The number of biggest keys to keep in full form. */
  public final int topKeys;
  /**
   * Keys must be bigger or equals to <code>minimum</code> to be
   * kept as a top entry.
   */
  public final Optional<Double> minimum;
  /** Whether or not to show the results in descending order. */
  public final boolean isDescending;
  /** The maximum number of entries per key to keep. */
  public final Optional<Integer> maxTopEntrySize;
  /** For PartWordExport: the maximum size a word list may have. */
  public final Optional<Integer> maxPartWordListSize;

  @Builder
  ExportParams(Integer topKeys, Optional<Double> minimum, Boolean isDescending, 
      Optional<Integer> maxTopEntrySize, Optional<Integer> maxPartWordListSize) {
    this.topKeys = useOrDefault(topKeys, 5);
    this.minimum = useOrDefault(minimum, Optional.empty());
    this.isDescending = useOrDefault(isDescending, true);
    this.maxTopEntrySize = useOrDefault(maxTopEntrySize, Optional.of(50));
    this.maxPartWordListSize = useOrDefault(maxPartWordListSize, Optional.empty());
  }
  
  private static <T> T useOrDefault(T builderValue, T defaultValue) {
    return builderValue != null ? builderValue : defaultValue;
  }

}
