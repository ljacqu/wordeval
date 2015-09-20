package ch.ljacqu.wordeval.evaluation.export;


public class ExportParamsBuilder {

  private int topKeys = 5;
  private Double minimum = null;
  private boolean isDescending = true;
  private Integer maxTopEntrySize = 50;
  private Integer maxPartWordListSize = null;

  public ExportParams build() {
    return new ExportParams(topKeys, minimum, isDescending, maxTopEntrySize,
        maxPartWordListSize);
  }

  public ExportParamsBuilder setTopKeys(int number) {
    this.topKeys = number;
    return this;
  }

  public ExportParamsBuilder setMinimum(Double minimum) {
    this.minimum = minimum;
    return this;
  }

  public ExportParamsBuilder setDescending(boolean isDescending) {
    this.isDescending = isDescending;
    return this;
  }

  public ExportParamsBuilder setMaxTopEntrySize(Integer maxEntry) {
    this.maxTopEntrySize = maxEntry;
    return this;
  }

  public ExportParamsBuilder setMaxPartWordListSize(Integer maxPartWordListSize) {
    this.maxPartWordListSize = maxPartWordListSize;
    return this;
  }

}
