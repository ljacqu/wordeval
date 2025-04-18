package ch.jalu.wordeval.evaluators.impl;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.result.WordGroupWithKey;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import java.util.Collection;

/**
 * Groups words which only differ in diacritics which are not considered
 * distinct letters in the language, such as {des, dés, dès} in French or
 * {schon, schön} in German.
 */
public class DiacriticHomonyms implements AllWordsEvaluator<WordGroupWithKey> {

  @Override
  public void evaluate(Collection<Word> words, ResultStore<WordGroupWithKey> resultStore) {
    SetMultimap<String, Word> wordsByNoAccentRep = words.stream()
      .collect(Multimaps.toMultimap(
        Word::getWithoutAccents,
        word -> word,
        HashMultimap::create));

    Multimaps.asMap(wordsByNoAccentRep).forEach((wordRep, wordsInGroup) -> {
      if (wordsInGroup.size() > 1) {
        resultStore.addResult(new WordGroupWithKey(wordsInGroup, wordRep));
      }
    });
  }
}
