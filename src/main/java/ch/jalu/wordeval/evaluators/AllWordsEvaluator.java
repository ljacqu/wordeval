package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.processing.ResultStore;

import java.util.List;

public interface AllWordsEvaluator {

  void evaluate(List<Word> words, ResultStore resultStore);
}
