package ch.jalu.wordeval.runners;

import ch.jalu.wordeval.evaluation.AlphabeticalSequence;
import ch.jalu.wordeval.evaluation.ConsecutiveVowelCount;
import ch.jalu.wordeval.evaluation.Evaluator;
import ch.jalu.wordeval.evaluation.FullPalindromes;
import ch.jalu.wordeval.evaluation.LongWords;
import ch.jalu.wordeval.evaluation.VowelCount;
import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link EvaluatorInitializer}.
 */
public class EvaluatorInitializerTest {

  @Test
  public void shouldInstantiateAllEvaluators() {
    // given
    Language language = mock(Language.class);
    given(language.getAlphabet()).willReturn(Alphabet.LATIN);
    given(language.getAdditionalVowels()).willReturn(emptyList());
    given(language.getAdditionalConsonants()).willReturn(emptyList());

    // when
    EvaluatorInitializer initializer = new EvaluatorInitializer(language);

    // then
    List<Evaluator<?>> evaluators = initializer.getEvaluators();
    assertThat(evaluators, hasSize(23));
    // Test presence for a sample of evaluators
    assertThat(evaluators, hasItem(instanceOf(AlphabeticalSequence.class)));
    assertThat(evaluators, hasItem(instanceOf(FullPalindromes.class)));
    assertThat(evaluators, hasItem(instanceOf(LongWords.class)));
    // Check that all enum values are present
    checkHasAllForEnum(evaluators, VowelCount.class, LetterType.class, VowelCount::getLetterType);
    checkHasAllForEnum(evaluators, ConsecutiveVowelCount.class, LetterType.class, ConsecutiveVowelCount::getLetterType);
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
  private static <T extends Evaluator<?>, E extends Enum<E>> void checkHasAllForEnum(
      List<Evaluator<?>> evaluators, Class<T> evaluatorClass, Class<E> enumClass, Function<T, E> getEnumValue) {
    List<E> foundEnumValues = evaluators.stream()
        .filter(e -> evaluatorClass == e.getClass())
        .map(evaluatorClass::cast)
        .map(getEnumValue)
        .collect(Collectors.toList());
    assertThat(foundEnumValues, containsInAnyOrder(enumClass.getEnumConstants()));
  }

}