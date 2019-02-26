package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluatedWord;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.WordEvaluator;
import com.google.common.collect.ImmutableMultimap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Manages a collection of evaluators and their results.
 */
@SuppressWarnings("unchecked")
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
      EvaluationResult result = evaluatorEntry.getKey().evaluate(word);
      if (result != null) {
        evaluatorEntry.getValue().addResult(word, result);
      }
    }
  }

  public void processPostEvaluators() {
    ResultsProvider resultsProvider = new ResultsProvider();
    for (Map.Entry<PostEvaluator, ResultStore> evaluatorEntry : postEvaluators.entrySet()) {
      PostEvaluator postEvaluator = evaluatorEntry.getKey();
      postEvaluator.evaluateAndSaveResults(resultsProvider, evaluatorEntry.getValue());
    }
  }

  public class ResultsProvider {

    public ImmutableMultimap<Double, EvaluatedWord> getResultsOfEvaluator(WordEvaluator evaluator) {
      return wordEvaluators.get(evaluator).getEntries();
    }

    public <W extends WordEvaluator> W findEvaluatorOfTypeMatching(Class<W> evaluatorClass,
                                                                   Predicate<W> predicate) {
      List<W> matchingEvaluators = wordEvaluators.keySet().stream()
          .filter(evaluatorClass::isInstance)
          .map(evaluatorClass::cast)
          .filter(predicate)
          .collect(Collectors.toList());
      if (matchingEvaluators.size() == 1) {
        return matchingEvaluators.get(0);
      } else if (matchingEvaluators.isEmpty()) {
        throw new IllegalStateException("Found no matching evaluator");
      } else {
        throw new IllegalStateException("Found " + matchingEvaluators.size() + " evaluators but expected only 1");
      }
    }
  }
}
