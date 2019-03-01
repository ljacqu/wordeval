package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultStoreImpl;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimaps;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class EvaluatorTestHelper {

  private EvaluatorTestHelper() {
  }

  public static ImmutableMultimap<Double, EvaluatedWord> evaluate(AllWordsEvaluator evaluator, String... words) {
    List<Word> allWords = Arrays.stream(words).map(TestWord::new).collect(Collectors.toList());
    ResultStore resultStore = new ResultStoreImpl();
    evaluator.evaluate(allWords, resultStore);
    return resultStore.getEntries();
  }

  public static HashMultimap<Double, String> evaluateToFlatResult(AllWordsEvaluator evaluator, String... words) {
    ImmutableMultimap<Double, EvaluatedWord> result = evaluate(evaluator, words);
    return result.values().stream()
      .map(EvaluatedWord::getResult)
      .collect(Multimaps.toMultimap(EvaluationResult::getScore, EvaluationResult::getKey, HashMultimap::create));
  }
}
