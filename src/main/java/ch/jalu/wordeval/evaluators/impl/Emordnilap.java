package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.result.WordGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Finds emordnilaps, words that produce another word when reversed,
 * such as German "Lager" and "Regal".
 */
public class Emordnilap implements AllWordsEvaluator<WordGroup> {

  @Override
  public void evaluate(Collection<Word> words, ResultStore<WordGroup> resultStore) {
    TreeMap<String, Word> wordsByLowercase = words.stream()
      .collect(Collectors.toMap(Word::getLowercase, word -> word, (a, b) -> b, TreeMap::new));

    for (Map.Entry<String, Word> entry : wordsByLowercase.entrySet()) {
      String lowercase = entry.getKey();
      String reversed = StringUtils.reverse(lowercase);
      Word reversedWord = wordsByLowercase.get(reversed);
      if (lowercase.compareTo(reversed) < 0 && reversedWord != null) {
        // TODO: Add smarter checks to avoid performing work, maybe can even stop once compareTo is < 0 ?
        resultStore.addResult(new WordGroup(entry.getValue(), reversedWord));
      }
    }
  }
}
