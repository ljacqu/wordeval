package ch.ljacqu.wordeval.evaluation.export;

public class ExportParams {

  /** The number of biggest keys to keep in full form. */
  public final int topKeys;
  /**
   * If not null, keys must be bigger or equals to <code>minimum</code> to be
   * kept in full form.
   */
  public final Integer minimum;
  /** Whether or not to show the results in descending order. */
  public final boolean isDescending;
  /** The maximum number of entries per key to keep. */
  public final Integer maxTopEntrySize;
  /** For PartWordExport: the maximum size a word list may have. */
  public final Integer maxPartWordListSize;

  ExportParams(int number, Integer minimum, boolean isDescending,
      Integer maxEntry, Integer maxPartWordListSize) {
    this.topKeys = number;
    this.minimum = minimum;
    this.isDescending = isDescending;
    this.maxTopEntrySize = maxEntry;
    this.maxPartWordListSize = maxPartWordListSize;
  }

}