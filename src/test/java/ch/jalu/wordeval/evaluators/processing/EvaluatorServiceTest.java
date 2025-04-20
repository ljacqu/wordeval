package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.dictionary.TestWord;
import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.evaluators.AllWordsEvaluator;
import ch.jalu.wordeval.evaluators.PostEvaluator;
import ch.jalu.wordeval.evaluators.impl.AlphabeticalSequence;
import ch.jalu.wordeval.evaluators.impl.ConsecutiveVowelCount;
import ch.jalu.wordeval.evaluators.impl.DiacriticHomonyms;
import ch.jalu.wordeval.evaluators.impl.FullPalindromes;
import ch.jalu.wordeval.evaluators.impl.LongWords;
import ch.jalu.wordeval.evaluators.impl.RepeatedSegmentConsecutive;
import ch.jalu.wordeval.evaluators.impl.SingleVowel;
import ch.jalu.wordeval.evaluators.impl.VowelCount;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link EvaluatorService}.
 */
@ExtendWith(MockitoExtension.class)
class EvaluatorServiceTest {

  @InjectMocks
  private EvaluatorService evaluatorService;

  @Test
  void shouldInstantiateAllEvaluators() {
    // given
    Language language = mock(Language.class);

    // when
    EvaluatorCollection initializer = evaluatorService.createAllEvaluators(language);

    // then
    assertThat(initializer.size(), equalTo(20));

    // Test presence for a sample of evaluators
    assertThat(initializer.allWordsEvaluators(), hasItem(instanceOf(AlphabeticalSequence.class)));
    assertThat(initializer.allWordsEvaluators(), hasItem(instanceOf(DiacriticHomonyms.class)));
    assertThat(initializer.allWordsEvaluators(), hasItem(instanceOf(LongWords.class)));
    assertThat(initializer.postEvaluators(), hasItem(instanceOf(RepeatedSegmentConsecutive.class)));
    assertThat(initializer.postEvaluators(), hasItem(instanceOf(SingleVowel.class)));
    assertThat(initializer.postEvaluators(), hasItem(instanceOf(FullPalindromes.class)));

    // Check that all enum values are present
    checkHasAllForEnum(initializer.allWordsEvaluators(), VowelCount.class, LetterType.class, VowelCount::getLetterType);
    checkHasAllForEnum(initializer.allWordsEvaluators(), ConsecutiveVowelCount.class, LetterType.class, ConsecutiveVowelCount::getLetterType);
    checkHasAllForEnum(initializer.postEvaluators(), SingleVowel.class, LetterType.class, SingleVowel::getLetterType);
  }

  @Test
  void shouldProcessAllWords() {
    // given
    AllWordsEvaluator evaluator1 = mock(AllWordsEvaluator.class);
    AllWordsEvaluator evaluator2 = mock(AllWordsEvaluator.class);
    PostEvaluator evaluator3 = mock(PostEvaluator.class);
    PostEvaluator evaluator4 = mock(PostEvaluator.class);
    EvaluatorCollection evaluators = new EvaluatorCollection(
        List.of(evaluator1, evaluator2), List.of(evaluator3, evaluator4));
    List<Word> words = List.of(new TestWord("apple"), new TestWord("banana"));

    // when
    evaluatorService.processAllWords(evaluators, words);

    // then
    InOrder inOrder = Mockito.inOrder(evaluator1, evaluator2, evaluator3, evaluator4);
    inOrder.verify(evaluator1).evaluate(words);
    inOrder.verify(evaluator2).evaluate(words);
    inOrder.verify(evaluator3).evaluate(evaluators);
    inOrder.verify(evaluator4).evaluate(evaluators);
    inOrder.verifyNoMoreInteractions();
  }

  /**
   * Checks that an evaluator instance of the given class is present for each value of the given enum.
   *
   * @param evaluators the list of evaluators
   * @param evaluatorClass the class of the evaluator to check
   * @param enumClass the class of the enum to check
   * @param getEnumValue getter of the evaluator's enum value
   * @param <T> the evaluator type
   * @param <E> the enum type
   */
  private static <T, E extends Enum<E>> void checkHasAllForEnum(List<? super T> evaluators, Class<T> evaluatorClass,
                                                                Class<E> enumClass, Function<T, E> getEnumValue) {
    List<E> foundEnumValues = evaluators.stream()
      .filter(e -> evaluatorClass == e.getClass())
      .map(evaluatorClass::cast)
      .map(getEnumValue)
      .collect(Collectors.toList());
    assertThat(foundEnumValues, containsInAnyOrder(enumClass.getEnumConstants()));
  }
}
