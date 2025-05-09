package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.export.EvaluatorExportUtil;
import ch.jalu.wordeval.evaluators.result.WordGroupWithKey;
import ch.jalu.wordeval.util.StreamUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Collects anagram groups (e.g. "acre", "care", "race").
 */
public class Anagrams implements AllWordsEvaluator {

  @Getter
  private final List<WordGroupWithKey> results = new ArrayList<>();

  @Override
  public void evaluate(Collection<Word> words) {
    SetMultimap<String, Word> wordsBySortedChars = words.stream()
      .collect(Multimaps.toMultimap(this::sortLettersAlphabetically, Function.identity(), HashMultimap::create));

    Multimaps.asMap(wordsBySortedChars).forEach((sequence, groupedWords) -> {
      if (groupedWords.size() > 1) {
        // Note: IntelliJ claims the .sorted call has no effect, but it ensures that lowercase words will be processed
        // before their uppercase counterparts.
        Set<Word> uniqueWords = groupedWords.stream()
            .sorted(Comparator.comparing(w -> Character.isUpperCase(w.getRaw().charAt(0))))
            .filter(StreamUtils.distinctByKey(Word::getLowercase))
            .collect(Collectors.toSet());
        results.add(new WordGroupWithKey(uniqueWords, sequence));
      }
    });
  }

  @Override
  public String getId() {
    return "group.anagrams";
  }

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
    Comparator<WordGroupWithKey> comparator = Comparator.comparingInt((WordGroupWithKey group) -> group.words().size())
        .thenComparing(group -> group.key().length())
        .reversed(); // todo: unit test

    List<WordGroupWithKey> sortedResult = results.stream()
        .sorted(comparator)
        .toList();

    Set<Integer> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = EvaluatorExportUtil.newListMultimap();
    for (WordGroupWithKey wordGroup : sortedResult) {
      int score = wordGroup.words().size();
      if (uniqueValues.add(score) && uniqueValues.size() > topScores) {
        break;
      }
      List<String> wordList = wordGroup.words().stream()
          .map(Word::getRaw)
          .toList();
      filteredResults.put(score, wordList);
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return filteredResults;
  }

  private String sortLettersAlphabetically(Word word) {
    char[] chars = word.getWithoutAccentsWordCharsOnly().toCharArray();
    Arrays.sort(chars);
    return new String(chars);
  }
}
