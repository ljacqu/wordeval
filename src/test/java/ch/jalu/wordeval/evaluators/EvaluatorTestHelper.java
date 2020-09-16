package ch.jalu.wordeval.evaluators;

import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.processing.ResultStore;
import ch.jalu.wordeval.evaluators.processing.ResultStoreImpl;
import ch.jalu.wordeval.evaluators.processing.ResultsProvider;
import ch.jalu.wordeval.evaluators.result.EvaluationResult;
import ch.jalu.wordeval.evaluators.result.WordGroup;
import ch.jalu.wordeval.evaluators.result.WordGroupWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithKey;
import ch.jalu.wordeval.evaluators.result.WordWithKeyAndScore;
import ch.jalu.wordeval.evaluators.result.WordWithScore;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public final class EvaluatorTestHelper {

  private EvaluatorTestHelper() {
  }

  public static Map<String, Set<String>> evaluateAndGroupWordsByKey(AllWordsEvaluator<WordGroupWithKey> evaluator,
                                                                    String... words) {
    return evaluate(evaluator, words).stream()
      .collect(Collectors.toMap(WordGroupWithKey::getKey, group -> unwrapWords(group.getWords())));
  }

  public static Map<String, Set<String>> evaluateAndGroupWordsByKey(AllWordsEvaluator<WordGroupWithKey> evaluator,
                                                                    List<Word> words) {
    ResultStore<WordGroupWithKey> resultStore = new ResultStoreImpl<>();
    evaluator.evaluate(words, resultStore);
    return resultStore.getEntries().stream()
      .collect(Collectors.toMap(WordGroupWithKey::getKey, group -> unwrapWords(group.getWords())));
  }

  public static Map<String, Set<String>> evaluateAndGroupByKey(AllWordsEvaluator<WordWithKey> evaluator,
                                                               String... words) {
    return evaluate(evaluator, words).stream()
      .collect(Collectors.groupingBy(WordWithKey::getKey))
      .entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey,
        e -> unwrapWords(e.getValue(), WordWithKey::getWord)));
  }

  public static Map<Double, Set<String>> evaluateAndGroupByScore(AllWordsEvaluator<WordWithScore> evaluator,
                                                                 String... words) {
    return evaluate(evaluator, words).stream().collect(Collectors.groupingBy(WordWithScore::getScore))
      .entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey,
        e -> unwrapWords(e.getValue(), WordWithScore::getWord)));
  }

  public static List<Set<String>> evaluateAndUnwrapWordGroups(AllWordsEvaluator<WordGroup> evaluator,
                                                              String... words) {
    return evaluate(evaluator, words).stream()
      .map(wordGroup -> unwrapWords(wordGroup.getWords()))
      .collect(Collectors.toList());
  }

  public static Map<String, Set<String>> evaluateAndFlattenKeyAndScore(AllWordsEvaluator<WordWithKeyAndScore> evaluator,
                                                                       String... words) {
    return evaluate(evaluator, words).stream()
      .collect(Collectors.groupingBy(wwks -> wwks.getScore() + "," + wwks.getKey())).entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, e -> unwrapWords(e.getValue(), WordWithKeyAndScore::getWord)));
  }

  public static <R extends EvaluationResult> ImmutableList<R> evaluatePostEvaluatorWithResults(
    PostEvaluator<R> evaluator, List<? extends EvaluationResult> resultsFromStore) {

    ResultsProvider resultsProvider = mock(ResultsProvider.class);
    given(resultsProvider.getResultsOfEvaluatorOfType(any(Class.class))).willReturn(ImmutableList.copyOf(resultsFromStore));
    given(resultsProvider.getResultsOfEvaluatorOfType(any(Class.class), any(Predicate.class))).willReturn(ImmutableList.copyOf(resultsFromStore));

    ResultStore<R> resultStore = new ResultStoreImpl<>();
    evaluator.evaluateAndSaveResults(resultsProvider, resultStore);
    return resultStore.getEntries();
  }

  private static <R extends EvaluationResult> ImmutableList<R> evaluate(AllWordsEvaluator<R> evaluator, String... words) {
    List<Word> allWords = Arrays.stream(words).map(TestWord::new).collect(Collectors.toList());
    ResultStore<R> resultStore = new ResultStoreImpl<>();
    evaluator.evaluate(allWords, resultStore);
    return resultStore.getEntries();
  }

  private static Set<String> unwrapWords(Collection<Word> words) {
    return words.stream()
      .map(Word::getRaw)
      .collect(Collectors.toSet());
  }

  private static <T> Set<String> unwrapWords(Collection<T> items, Function<T, Word> wordGetter) {
    return unwrapWords(items.stream().map(wordGetter).collect(Collectors.toSet()));
  }
}
