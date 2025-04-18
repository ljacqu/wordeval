package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.export.EvaluatorExportUtil;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import com.google.common.collect.ListMultimap;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Finds emordnilaps, words that produce another word when reversed,
 * such as German "Lager" and "Regal".
 */
public class Emordnilap implements AllWordsEvaluator {

  @Getter
  private final List<WordWithKey> results = new ArrayList<>();

  @Override
  public void evaluate(Collection<Word> words) {
    TreeMap<String, Word> wordsByLowercase = words.stream()
      .collect(Collectors.toMap(Word::getLowercase, word -> word, (a, b) -> b, TreeMap::new));

    for (Map.Entry<String, Word> entry : wordsByLowercase.entrySet()) {
      String lowercase = entry.getKey();
      String reversed = StringUtils.reverse(lowercase);
      Word reversedWord = wordsByLowercase.get(reversed);
      if (lowercase.compareTo(reversed) < 0 && reversedWord != null) {
        // TODO: Add smarter checks to avoid performing work, maybe can even stop once compareTo is < 0 ?
        results.add(new WordWithKey(reversedWord, entry.getKey()));
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
