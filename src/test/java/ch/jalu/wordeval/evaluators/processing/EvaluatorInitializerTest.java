package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.evaluators.impl.AllVowels;
import ch.jalu.wordeval.evaluators.impl.AlphabeticalSequence;
import ch.jalu.wordeval.evaluators.impl.ConsecutiveVowelCount;
import ch.jalu.wordeval.evaluators.impl.DiacriticHomonyms;
import ch.jalu.wordeval.evaluators.impl.FullPalindromes;
import ch.jalu.wordeval.evaluators.impl.LongWords;
import ch.jalu.wordeval.evaluators.impl.RepeatedSegmentConsecutive;
import ch.jalu.wordeval.evaluators.impl.SingleVowel;
import ch.jalu.wordeval.evaluators.impl.VowelCount;
import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LetterType;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
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
    assertThat(initializer.getEvaluatorsCount(), equalTo(22));

    // Test presence for a sample of evaluators
    assertThat(initializer.getAllWordsEvaluators(), hasItem(instanceOf(AlphabeticalSequence.class)));
    assertThat(initializer.getAllWordsEvaluators(), hasItem(instanceOf(DiacriticHomonyms.class)));
    assertThat(initializer.getAllWordsEvaluators(), hasItem(instanceOf(LongWords.class)));
    assertThat(initializer.getPostEvaluators(), hasItem(instanceOf(RepeatedSegmentConsecutive.class)));
    assertThat(initializer.getPostEvaluators(), hasItem(instanceOf(SingleVowel.class)));
    assertThat(initializer.getPostEvaluators(), hasItem(instanceOf(FullPalindromes.class)));

    // Check that all enum values are present
    checkHasAllForEnum(initializer.getAllWordsEvaluators(), VowelCount.class, LetterType.class, VowelCount::getLetterType);
    checkHasAllForEnum(initializer.getAllWordsEvaluators(), ConsecutiveVowelCount.class, LetterType.class, ConsecutiveVowelCount::getLetterType);
    checkHasAllForEnum(initializer.getPostEvaluators(), AllVowels.class, LetterType.class, AllVowels::getLetterType);
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