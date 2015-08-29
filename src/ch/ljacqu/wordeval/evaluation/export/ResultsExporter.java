package ch.ljacqu.wordeval.evaluation.export;

import com.google.gson.Gson;

public class ResultsExporter {

  public String toJson(ExportObject expRes) {
    Gson gson = new Gson();
    return gson.toJson(expRes);
  }

}
