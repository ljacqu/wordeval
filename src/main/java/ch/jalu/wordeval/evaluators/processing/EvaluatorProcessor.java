package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.Evaluator;
import ch.jalu.wordeval.evaluators.PostEvaluator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages a collection of evaluators and their results.
 */
public class EvaluatorProcessor {

  private final Map<AllWordsEvaluator<?>, ResultStore<?>> wordEvaluators;
  private final Map<PostEvaluator<?>, ResultStore<?>> postEvaluators;

  public EvaluatorProcessor(Collection<AllWordsEvaluator<?>> wordEvaluators,
                            Collection<PostEvaluator<?>> postEvaluators) {
    this.wordEvaluators = wordEvaluators.stream()
      .collect(Collectors.toMap(Function.identity(), k -> new ResultStoreImpl<>()));
    this.postEvaluators = postEvaluators.stream()
      .collect(Collectors.toMap(Function.identity(), k -> new ResultStoreImpl<>()));
  }

  public EvaluatorProcessor(EvaluatorInitializer evaluatorInitializer) {
    this(evaluatorInitializer.getAllWordsEvaluators(), evaluatorInitializer.getPostEvaluators());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void processAllWords(Collection<Word> words) {
    wordEvaluators.forEach((evaluator, resultStore) -> evaluator.evaluate(words, (ResultStore) resultStore));

    // TODO: Migrate results provider
    ResultsProvider resultsProvider = new ResultsProvider(wordEvaluators);
    postEvaluators.forEach(
        (evaluator, resultStore) -> evaluator.evaluateAndSaveResults(resultsProvider, (ResultStore) resultStore));
  }

  public Map<Evaluator<?>, ResultStore<?>> getAllResults() {
    // TODO: Probably unused
    return Stream.concat(wordEvaluators.entrySet().stream(), postEvaluators.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public List<Evaluator<?>> getAllEvaluators() {
    return Stream.concat(wordEvaluators.keySet().stream(), postEvaluators.keySet().stream())
        .toList();
  }
}
