package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Dummy evaluator that gathers all words from a dictionary for further processing.
 */
@Deprecated // use AllWordsEvaluator
public class WordCollector extends DictionaryEvaluator<Boolean> {

  private List<String> words = new LinkedList<>();

  /**
   * Gets the words from a dictionary and returns them in a sorted manner.
   *
   * @return The list of sorted words
   */
  public List<String> returnSortedWords() {
    Collections.sort(words);
    return words;
  }
  
  @Override
  public void processWord(String word, String rawWord) {
    words.add(word);
  }

  @Override
  protected ExportObject toExportObject(String identifier, ExportParams params) {
    return null;
  }

}
