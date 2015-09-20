package ch.ljacqu.wordeval.evaluation.export;

/**
 * The export parameters, defining how the information of an evaluator's results
 * should be kept.
 */
public class ExportParams {

  /** The number of biggest keys to keep in full form. */
  public final int topKeys;
  /**
   * If not null, keys must be bigger or equals to <code>minimum</code> to be
   * kept as a top entry.
   */
  public final Double minimum;
  /** Whether or not to show the results in descending order. */
  public final boolean isDescending;
  /** The maximum number of entries per key to keep. */
  public final Integer maxTopEntrySize;
  /** For PartWordExport: the maximum size a word list may have. */
  public final Integer maxPartWordListSize;

  ExportParams(int number, Double minimum, boolean isDescending, Integer maxEntry, Integer maxPartWordListSize) {
    this.topKeys = number;
    this.minimum = minimum;
    this.isDescending = isDescending;
    this.maxTopEntrySize = maxEntry;
    this.maxPartWordListSize = maxPartWordListSize;
  }

}
