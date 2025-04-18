package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
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
import java.util.stream.Collectors;

/**
 * Evaluator which collects all words by count of
 * separate vowels or consonants for further processing.
 */
public class VowelCount implements WordEvaluator<WordWithKey> {

  private final List<WordWithKey> results = new ArrayList<>();

  private final List<String> letters;
  @Getter
  private final LetterType letterType;

  public VowelCount(Language language, LetterType letterType) {
    this.letters = letterType.getLetters(language);
    this.letterType = letterType;
  }

  @Override
  public void evaluate(Word word, ResultStore<WordWithKey> resultStore) {
    // TODO #64: Iterate over the letters of the word instead
    String wordWithoutAccents = word.getWithoutAccents();
    String letterProfile = letters.stream()
      .filter(wordWithoutAccents::contains)
      .collect(Collectors.joining());
    resultStore.addResult(new WordWithKey(word, letterProfile));
    results.add(new WordWithKey(word, letterProfile));
  }

  @Override
  public ListMultimap<Object, Object> getTopResults(int topScores, int maxLimit) {
    // TODO: Should probably skip this as PostEvaluators cover the interesting stuff
    List<WordWithKey> sortedResult = results.stream()
        .sorted(Comparator.comparing((WordWithKey wwk) -> wwk.getKey().length()).reversed())
        .toList();

    Set<String> uniqueValues = new HashSet<>();
    ListMultimap<Object, Object> filteredResults = ArrayListMultimap.create();
    for (WordWithKey WordWithKey : sortedResult) {
      if (uniqueValues.add(WordWithKey.getKey()) && uniqueValues.size() > topScores) {
        break;
      }
      filteredResults.put(WordWithKey.getKey(), WordWithKey.getWord().getRaw());
      if (filteredResults.size() >= maxLimit) {
        break;
      }
    }

    return filteredResults;
  }

  @Override
  public String getId() {
    return switch (letterType) {
      case VOWELS -> "VowelCount";
      case CONSONANTS -> "ConsonantCount";
    };
  }
}
