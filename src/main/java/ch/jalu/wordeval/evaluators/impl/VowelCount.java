package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Evaluator which collects all words by count of
 * separate vowels or consonants for further processing.
 */
public class VowelCount implements WordEvaluator {

  private final List<String> letters;
  @Getter
  private final LetterType letterType;

  public VowelCount(Language language, LetterType letterType) {
    this.letters = letterType.getLetters(language);
    this.letterType = letterType;
  }

  @Override
  public void evaluate(Word word, ResultStore resultStore) {
    // TODO #64: Iterate over the letters of the word instead
    String wordWithoutAccents = word.getWithoutAccents();
    String letterProfile = letters.stream()
      .filter(wordWithoutAccents::contains)
      .collect(Collectors.joining());
    resultStore.addResult(word, new EvaluationResult(letterProfile.length(), letterProfile));
  }
}
