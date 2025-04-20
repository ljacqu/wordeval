package ch.jalu.wordeval.evaluators.processing;

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
import org.mockito.InjectMocks;
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
 * Test for {@link EvaluatorInitializer}.
 */
@ExtendWith(MockitoExtension.class)
class EvaluatorInitializerTest {

  @InjectMocks
  private EvaluatorInitializer evaluatorInitializer;

  @Test
  void shouldInstantiateAllEvaluators() {
    // given
    Language language = mock(Language.class);

    // when
    EvaluatorCollection initializer = evaluatorInitializer.createAllEvaluators(language);

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