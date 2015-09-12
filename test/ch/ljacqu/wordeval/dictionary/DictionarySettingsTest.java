package ch.ljacqu.wordeval.dictionary;

import org.junit.Test;
import ch.ljacqu.wordeval.evaluation.PartWordEvaluator;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;

public class DictionarySettingsTest {

  // TODO: Create test with mocks to ensure that evaluators are always called

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionForUnknownDictionary() {
    DictionarySettings.get("non-existent");
  }

  static class TestEvaluator extends PartWordEvaluator {

    @Override
    public void processWord(String word, String rawWord) {
      // Auto-generated method stub

    }

    @Override
    protected ExportObject toExportObject(String identifier, ExportParams params) {
      // Auto-generated method stub
      return null;
    }

  }

}
