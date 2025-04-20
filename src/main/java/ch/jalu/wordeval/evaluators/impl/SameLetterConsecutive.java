package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.export.EvaluatorExportUtil;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import com.google.common.collect.ListMultimap;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Finds words wherein the same letter appears multiple times consecutively,
 * e.g. "lll" in German "Rollladen."
 */
public class SameLetterConsecutive implements WordEvaluator {

  @Getter
  private final List<WordWithKey> results = new ArrayList<>();

  @Override
  public void evaluate(Word wordObject) {
    String word = wordObject.getWithoutAccents();
    int counter = 0;
    char lastChar = '\0';
    for (int i = 0; i <= word.length(); ++i) {
      if (i < word.length() && word.charAt(i) == lastChar) {
        ++counter;
      } else {
        if (counter > 1) {
          results.add(new WordWithKey(wordObject, StringUtils.repeat(lastChar, counter)));
        }
        lastChar = i < word.length() ? word.charAt(i) : '\0';
        counter = 1;
      }
    }
  }

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
    List<WordWithKey> sortedResult = results.stream()
        .sorted(Comparator.<WordWithKey>comparingInt(wordWithKey -> wordWithKey.getKey().length()).reversed())
        .toList();

    Set<Integer> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = EvaluatorExportUtil.newListMultimap();
    for (WordWithKey wordWithKey : sortedResult) {
      int score = wordWithKey.getKey().length();
      if (uniqueValues.add(score) && uniqueValues.size() > topScores) {
        break;
      }
      filteredResults.put(score, wordWithKey.getWord().getRaw() + " (" + wordWithKey.getKey() + ")");
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return filteredResults;
  }
}
