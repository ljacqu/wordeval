package ch.ljacqu.wordeval.evaluation.export;

import lombok.Builder;

/**
 * The export parameters, defining how the information of an evaluator's results
 * should be kept.
 */
public class ExportParams {

  /** The number of biggest keys to keep in full form. */
  public final int topKeys;
  /**
   * If not negative, keys must be bigger or equals to <code>minimum</code> to be
   * kept as a top entry.
   */
  @SkipIfNegative
  public final double minimum;
  /** Whether or not to show the results in descending order. */
  public final boolean isDescending;
  /** The maximum number of entries per key to keep. */
  @SkipIfNegative
  public final int maxTopEntrySize;
  /** For PartWordExport: the maximum size a word list may have. */
  @SkipIfNegative
  public final int maxPartWordListSize;

  @Builder
  ExportParams(Integer topKeys, Double minimum, Boolean isDescending, 
      Integer maxTopEntrySize, Integer maxPartWordListSize) {
    this.topKeys = useOrDefault(topKeys, 5);
    this.minimum = useOrDefault(minimum, -1.0);
    this.isDescending = useOrDefault(isDescending, true);
    this.maxTopEntrySize = useOrDefault(maxTopEntrySize, 50);
    this.maxPartWordListSize = useOrDefault(maxPartWordListSize, -1);
  }
  
  private static <T> T useOrDefault(T builderValue, T defaultValue) {
    return builderValue != null ? builderValue : defaultValue;
  }

}
