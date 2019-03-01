package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Groups words which only differ in diacritics which are not considered
 * distinct letters in the language, such as {des, dés, dès} in French or
 * {schon, schön} in German.
 */
public class DiacriticHomonyms implements AllWordsEvaluator {

  @Override
  public void evaluate(Collection<Word> words, ResultStore resultStore) {
    HashMultimap<String, Word> wordsByNoAccentRep = words.stream()
      .collect(Multimaps.toMultimap(
        Word::getWithoutAccents,
        word -> word,
        HashMultimap::create));

    wordsByNoAccentRep.asMap().forEach((wordRep, wordsInGroup) -> {
      if (wordsInGroup.size() > 1) {
        String wordList = wordsInGroup.stream()
          .map(Word::getRaw)
          .collect(Collectors.joining(", "));
        resultStore.addResult(null, new EvaluationResult(wordsInGroup.size(), wordList));
      }
    });
  }
}
