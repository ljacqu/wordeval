package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.Evaluator;
import ch.jalu.wordeval.evaluators.PostEvaluator;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Manages evaluators and triggers their evaluation process.
 */
public class EvaluatorProcessor {

  private final List<AllWordsEvaluator> wordEvaluators;
  private final List<PostEvaluator> postEvaluators;

  public EvaluatorProcessor(Collection<AllWordsEvaluator> wordEvaluators,
                            Collection<PostEvaluator> postEvaluators) {
    this.wordEvaluators = List.copyOf(wordEvaluators);
    this.postEvaluators = List.copyOf(postEvaluators);
  }

  public EvaluatorProcessor(EvaluatorInitializer evaluatorInitializer) {
    this(evaluatorInitializer.getAllWordsEvaluators(), evaluatorInitializer.getPostEvaluators());
  }

  public void processAllWords(Collection<Word> words) {
    wordEvaluators.forEach(evaluator -> evaluator.evaluate(words));

    AllWordsEvaluatorProvider allWordsEvaluatorProvider = new AllWordsEvaluatorProvider(wordEvaluators);
    postEvaluators.forEach(evaluator -> evaluator.evaluate(allWordsEvaluatorProvider));
  }

  public List<Evaluator> getAllEvaluators() {
    return Stream.concat(wordEvaluators.stream(), postEvaluators.stream())
        .toList();
  }
}
