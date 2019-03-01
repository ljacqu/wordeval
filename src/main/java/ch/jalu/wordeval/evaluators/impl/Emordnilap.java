package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Finds emordnilaps, words that produce another word when reversed,
 * such as German "Lager" and "Regal".
 */
public class Emordnilap implements AllWordsEvaluator {

  @Override
  public void evaluate(List<Word> words, ResultStore resultStore) {
    TreeMap<String, Word> wordsByLowercase = words.stream()
      .collect(Collectors.toMap(Word::getLowercase, word -> word, (a, b) -> b, TreeMap::new));

    for (Map.Entry<String, Word> entry : wordsByLowercase.entrySet()) {
      String lowercase = entry.getKey();
      String reversed = StringUtils.reverse(lowercase);
      if (lowercase.compareTo(reversed) < 0 && wordsByLowercase.containsKey(reversed)) {
        resultStore.addResult(entry.getValue(), new EvaluationResult(lowercase.length(),
          wordsByLowercase.get(reversed).getRaw()));
      }
    }
  }
}
