package ch.ljacqu.wordeval.anagrams;

import static java.util.Arrays.asList;
import java.io.IOException;
import ch.ljacqu.wordeval.dictionary.Dictionary;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;
import ch.ljacqu.wordeval.evaluation.export.ExportParamsBuilder;
import ch.ljacqu.wordeval.evaluation.export.PartWordExport;
import ch.ljacqu.wordeval.evaluation.export.PartWordReducer;
import com.google.gson.Gson;

public class TestDriver {
  
  public static void main(String[] args) throws IOException {
    collectAnagrams("en-us");
  }

  public static void collectAnagrams(String languageCode) throws IOException {
    AnagramCollector collector = new AnagramCollector();
    Dictionary dictionary = Dictionary.getDictionary(languageCode, asList(collector));

    dictionary.process();

    Gson gson = new Gson();
    // TODO #14: Get biggest groups instead of biggest length, or combine both
    System.out.println(gson.toJson(collector.toExportObject()));
  }

}
