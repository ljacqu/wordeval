package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Searches words for clusters of vowels or consonants, e.g. "ngstschw" in
 * German "Angstschweiss". The same word can appear multiple times in the
 * results, e.g. "poignée" will count twice ("oi", "ée").
 */
public class ConsecutiveVowelCount implements WordEvaluator {

  private Set<String> lettersToConsider;
  @Getter
  private LetterType letterType;

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
  public void evaluate(Word wordObject, ResultStore resultStore) {
    String word = wordObject.getWithoutAccents();
    int count = 0;
    for (int i = 0; i <= word.length(); ++i) {
      if (i == word.length() || !lettersToConsider.contains(word.substring(i, i + 1))) {
        if (count > 1) {
          resultStore.addResult(wordObject, new EvaluationResult(count, null));
        }
        count = 0;
      } else {
        ++count;
      }
    }
  }
}
