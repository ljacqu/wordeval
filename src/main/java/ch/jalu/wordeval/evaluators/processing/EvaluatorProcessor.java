package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.WordEvaluator;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Manages a collection of evaluators and their results.
 */
public class EvaluatorProcessor {

  private final Map<WordEvaluator, ResultStore> wordEvaluators;
  private final Map<PostEvaluator, ResultStore> postEvaluators;

  public EvaluatorProcessor(Collection<WordEvaluator> wordEvaluators, Collection<PostEvaluator> postEvaluators) {
    this.wordEvaluators = wordEvaluators.stream()
      .collect(Collectors.toMap(Function.identity(), k -> new ResultStoreImpl()));
    this.postEvaluators = postEvaluators.stream()
      .collect(Collectors.toMap(Function.identity(), k -> new ResultStoreImpl()));
  }

  public void processWord(Word word) {
    for (Map.Entry<WordEvaluator, ResultStore> evaluatorEntry : wordEvaluators.entrySet()) {
      evaluatorEntry.getKey().evaluate(word, evaluatorEntry.getValue());
    }
  }

  public void processPostEvaluators() {
    ResultsProvider resultsProvider = new ResultsProvider(wordEvaluators);
    for (Map.Entry<PostEvaluator, ResultStore> evaluatorEntry : postEvaluators.entrySet()) {
      PostEvaluator postEvaluator = evaluatorEntry.getKey();
      postEvaluator.evaluateAndSaveResults(resultsProvider, evaluatorEntry.getValue());
    }
  }

  Map<WordEvaluator, ResultStore> getWordEvaluators() {
    return wordEvaluators;
  }
}
