package ch.jalu.wordeval.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.jalu.wordeval.dictionary.WordForm;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.evaluation.export.ExportObject;

/**
 * Dummy evaluator that gathers all words from a dictionary for further processing.
 */
public class WordCollector extends Evaluator<Boolean> {

  /**
   * Gets the words from a dictionary and returns them in a sorted manner.
   * Use {@link #getSortedWordsFromDictionary(String)} if you do not pass this
   * evaluator to a dictionary manually. 
   * @return The list of sorted words
   */
  public List<String> returnSortedWords() {
    List<String> wordList = new ArrayList<>(getResults().get(Boolean.TRUE));
    Collections.sort(wordList);
    return wordList;
  }
  
  /**
   * Gets the words from the given dictionary and returns a sorted list of its words.
   * @param dictionaryCode The code of the dictionary to use
   * @return The list of sorted words
   */
  public List<String> getSortedWordsFromDictionary(String dictionaryCode) {
    Dictionary dict = Dictionary.getDictionary(dictionaryCode);
    dict.process(Collections.singleton(this));
    return returnSortedWords();
  }
  
  @Override
  public void processWord(String word, String rawWord) {
    addEntry(Boolean.TRUE, word);
  }
  
  @Override
  public WordForm getWordForm() {
    return WordForm.RAW;
  }

  @Override
  protected ExportObject toExportObject(String identifier, ExportParams params) {
    return null;
  }

}
