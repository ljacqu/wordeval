package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.EvaluatedWord;
import ch.jalu.wordeval.evaluators.EvaluationResult;
import ch.jalu.wordeval.evaluators.Evaluator;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.WordEvaluator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages a collection of evaluators and their results.
 */
@SuppressWarnings("unchecked")
public class EvaluatorProcessor {

  private final Map<WordEvaluator, ResultStore> wordEvaluators = new HashMap<>();
  private final Map<PostEvaluator, ResultStore> postEvaluators = new HashMap<>();

  public EvaluatorProcessor(Iterable<Evaluator> evaluators) {
    for (Evaluator evaluator : evaluators) {
      if (evaluator instanceof WordEvaluator) {
        wordEvaluators.put((WordEvaluator) evaluator, new ResultStoreImpl());
      } else if (evaluator instanceof PostEvaluator) {
        postEvaluators.put((PostEvaluator) evaluator, new ResultStoreImpl());
      } else {
        throw new IllegalArgumentException("Evaluator of class '" + evaluator.getClass()
            + "' does not implement a known evaluator subtype");
      }
    }
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
    for (Map.Entry<PostEvaluator, ResultStore> evaluatorEntry : postEvaluators.entrySet()) {
      evaluatorEntry.getValue().addResults(
        processEvaluator(evaluatorEntry.getKey(), wordEvaluators));
    }
  }

  private static Collection<EvaluatedWord> processEvaluator(PostEvaluator postEvaluator,
                                                            Map<WordEvaluator, ResultStore> wordEvaluators) {
    for (Map.Entry<WordEvaluator, ResultStore> wordEvaluatorEntry : wordEvaluators.entrySet()) {
      WordEvaluator wordEvaluator = wordEvaluatorEntry.getKey();
      if (postEvaluator.getBaseClass().isAssignableFrom(wordEvaluator.getClass())
          && postEvaluator.isBaseMatch(wordEvaluator)) {
        return postEvaluator.evaluate(wordEvaluator, wordEvaluatorEntry.getValue());
      }
    }
    throw new IllegalStateException("Could not find any matching base evaluator for post evaluator of type '"
       + postEvaluator.getClass() + "'");
  }

}
