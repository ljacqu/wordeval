package ch.ljacqu.wordeval.evaluation.export;

public class ExportParamsBuilder {

  private int number = 5;
  private Integer minimum = null;
  private boolean isDescending = false;
  private Integer maxEntry = null;

  public ExportParams build() {
    return new ExportParams(number, minimum, isDescending, maxEntry);
  }

  public ExportParamsBuilder setNumber(int number) {
    this.number = number;
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

  public ExportParamsBuilder setMaxEntry(Integer maxEntry) {
    this.maxEntry = maxEntry;
    return this;
  }

}
