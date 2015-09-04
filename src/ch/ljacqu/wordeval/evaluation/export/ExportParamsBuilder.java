package ch.ljacqu.wordeval.evaluation.export;

public class ExportParamsBuilder {

  private int topKeys = 5;
  private Integer minimum = null;
  private boolean isDescending = false;
  private Integer maxTopEntrySize = 50;

  public ExportParams build() {
    return new ExportParams(topKeys, minimum, isDescending, maxTopEntrySize);
  }

  public ExportParamsBuilder setTopKeys(int number) {
    this.topKeys = number;
    return this;
  }

  public ExportParamsBuilder setMinimum(Integer minimum) {
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

}
