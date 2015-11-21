package ch.ljacqu.wordeval.evaluation;

import java.util.Locale;
import java.util.Optional;

import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;
import ch.ljacqu.wordeval.evaluation.export.PartWordExport;
import ch.ljacqu.wordeval.evaluation.export.PartWordReducer;
import lombok.AllArgsConstructor;

/**
 * Groups words which only differ in diacritics which are not considered
 * distinct letters in the language, such as {des, dés, dès} in French or
 * {schon, schön} in German.
 */
@AllArgsConstructor
public class DiacriticHomonyms extends PartWordEvaluator {

  private Locale locale;

  @Override
  public void processWord(String word, String rawWord) {
    String lowerRawWord = rawWord.toLowerCase(locale);
    addEntry(word, lowerRawWord);
  }

  @Override
  public WordForm getWordForm() {
    return WordForm.NO_ACCENTS;
  }

  @Override
  public ExportObject toExportObject() {
    ExportParams params = ExportParams.builder()
      .topEntryMinimum(Optional.of(2.0))
      .build();

    return PartWordExport.create(
        getClass().getSimpleName(), 
        getResults(), 
        params, 
        new PartWordReducer.BySizeAndLength(1.0, 0.01));
  }

}
