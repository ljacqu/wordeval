package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.dictionary.WordForm;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for invoking the given evaluators properly.
 */
@Log4j2
public class EvaluatorInvoker {

  private List<DictionaryEvaluator<?>> dictionaryEvaluators;
  private List<PostEvaluator<?, ?>> postEvaluators;

  /**
   * Constructor.
   *
   * @param evaluators collection of evaluators to manage
   */
  public EvaluatorInvoker(Iterable<Evaluator<?>> evaluators) {
    separateEvaluators(evaluators);
  }

  /**
   * Lets all dictionary evaluators process the given word.
   *
   * @param word the word to process
   */
  public void processWord(Word word) {
    dictionaryEvaluators.forEach(evaluator -> {
      WordForm wordForm = evaluator.getWordForm();
      evaluator.processWord(word.getForm(wordForm), word.getForm(WordForm.RAW));
    });
  }

  private void separateEvaluators(Iterable<Evaluator<?>> evaluators) {
    dictionaryEvaluators = new ArrayList<>();
    postEvaluators = new ArrayList<>();
    for (Evaluator<?> evaluator : evaluators) {
      if (evaluator instanceof DictionaryEvaluator<?>) {
        dictionaryEvaluators.add((DictionaryEvaluator<?>) evaluator);
      } else if (evaluator instanceof PostEvaluator<?, ?>) {
        postEvaluators.add((PostEvaluator<?, ?>) evaluator);
      } else {
        throw new IllegalStateException("Evaluator of class '"
            + evaluator.getClass() + "' does not implement a known subtype.");
      }
    }
  }

  /**
   * Finds a suitable base for each post evaluator and executes them.
   */
  public void executePostEvaluators() {
    for (PostEvaluator<?, ?> postEvaluator : postEvaluators) {
      if (!findMatchAndExecute(postEvaluator, dictionaryEvaluators)
          && !findMatchAndExecute(postEvaluator, postEvaluators)) {
        throw new IllegalStateException("Did not find a suitable base for post evaluator '"
            + postEvaluator.getClass() + "'");
      }
    }
  }

  private static <T extends Evaluator<?>> boolean findMatchAndExecute(PostEvaluator<?, ?> postEvaluator,
                                                                      List<T> evaluators) {
    for (Evaluator<?> dictionaryEvaluator : evaluators) {
      if (postEvaluator.isBaseMatch(dictionaryEvaluator)) {
        postEvaluator.castAndEvaluate(dictionaryEvaluator);
        return true;
      }
    }
    return false;
  }

}
