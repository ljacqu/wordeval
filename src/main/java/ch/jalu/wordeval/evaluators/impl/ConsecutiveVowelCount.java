package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.result.WordWithScore;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Searches words for clusters of vowels or consonants, e.g. "ngstschw" in
 * German "Angstschweiss". The same word can appear multiple times in the
 * results, e.g. "poignée" will count twice ("oi", "ée").
 */
public class ConsecutiveVowelCount implements WordEvaluator<WordWithScore> {

  private final Set<String> lettersToConsider;
  @Getter
  private final LetterType letterType;
  private final List<WordWithScore> results = new ArrayList<>();

  /**
   * Creates a new VowelCount evaluator instance.
   *
   * @param letterType the letter type to consider
   * @param language the language of the words to evaluate
   */
  public ConsecutiveVowelCount(LetterType letterType, Language language) {
    this.lettersToConsider = new HashSet<>(letterType.getLetters(language));
    this.letterType = letterType;
  }

  @Override
  public void evaluate(Word wordObject, ResultStore<WordWithScore> resultStore) {
    String word = wordObject.getWithoutAccents();
    int count = 0;
    for (int i = 0; i <= word.length(); ++i) {
      if (i == word.length() || !lettersToConsider.contains(word.substring(i, i + 1))) {
        if (count > 1) {
          resultStore.addResult(new WordWithScore(wordObject, count));
          results.add(new WordWithScore(wordObject, count));
        }
        count = 0;
      } else {
        ++count;
      }
    }
  }

  @Override
  public List<ListMultimap<Object, Object>> getTopResults(int topScores, int maxLimit) {
    List<WordWithScore> sortedResult = results.stream()
        .sorted(Comparator.comparing(WordWithScore::getScore).reversed())
        .toList();

    Set<Double> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = ArrayListMultimap.create();
    for (WordWithScore wordWithScore : sortedResult) {
      if (uniqueValues.add(wordWithScore.getScore()) && uniqueValues.size() > topScores) {
        break;
      }
      filteredResults.put((int) wordWithScore.getScore(), wordWithScore.getWord().getRaw());
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return List.of(filteredResults);
  }

  @Override
  public String getId() {
    return switch (letterType) {
      case VOWELS -> "ConsecutiveVowelCount";
      case CONSONANTS -> "ConsecutiveConsonantCount";
    };
  }
}
