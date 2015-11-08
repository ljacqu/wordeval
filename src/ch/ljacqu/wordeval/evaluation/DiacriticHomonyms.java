package ch.ljacqu.wordeval.evaluation;

import java.util.Locale;
import java.util.Optional;

import ch.ljacqu.wordeval.dictionary.WordForm;
import ch.ljacqu.wordeval.evaluation.export.ExportObject;
import ch.ljacqu.wordeval.evaluation.export.ExportParams;
import ch.ljacqu.wordeval.evaluation.export.PartWordExport;
import ch.ljacqu.wordeval.evaluation.export.PartWordReducer;
import ch.ljacqu.wordeval.language.Language;

/**
 * Groups words which only differ in diacritics which are not considered
 * distinct letters in the language, such as {des, dés, dès} in French or
 * {schon, schön} in German.
 */
public class DiacriticHomonyms extends PartWordEvaluator {

  private Locale locale;

  /**
   * Creates a new instance of the evaluator for the given language.
   * @param language the language of the words to process
   */
  public DiacriticHomonyms(Language language) {
    // TODO #55: Pass Locale instead so it isn't built every time it is required
    locale = language.buildLocale();
  }

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
