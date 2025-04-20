package ch.jalu.wordeval.evaluators.processing;

import ch.jalu.wordeval.evaluators.Evaluator;
import ch.jalu.wordeval.evaluators.impl.Emordnilap;
import ch.jalu.wordeval.evaluators.impl.FullPalindromes;
import ch.jalu.wordeval.evaluators.impl.Palindromes;
import ch.jalu.wordeval.evaluators.impl.VowelCount;
import ch.jalu.wordeval.language.LetterType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Test for {@link EvaluatorCollection}.
 */
class EvaluatorCollectionTest {

  @Test
  void shouldReturnSize() {
    // given
    EvaluatorCollection collection = new EvaluatorCollection(
        List.of(mock(Palindromes.class), mock(Emordnilap.class)), List.of(mock(FullPalindromes.class)));

    // when
    int result = collection.size();

    // then
    assertThat(result, equalTo(3));
  }

  @Test
  void shouldStreamThroughAllEvaluators() {
    // given
    Emordnilap emordnilap = mock(Emordnilap.class);
    Palindromes palindromes = mock(Palindromes.class);
    FullPalindromes fullPalindromes = mock(FullPalindromes.class);
    EvaluatorCollection collection = new EvaluatorCollection(List.of(palindromes, emordnilap), List.of(fullPalindromes));

    // when
    List<Evaluator> result = collection.streamThroughAllEvaluators()
        .toList();

    // then
    assertThat(result, contains(palindromes, emordnilap, fullPalindromes));
  }

  @Test
  void shouldReturnEvaluator() {
    // given
    Emordnilap emordnilap = mock(Emordnilap.class);
    Palindromes palindromes = mock(Palindromes.class);
    FullPalindromes fullPalindromes = mock(FullPalindromes.class);
    EvaluatorCollection collection = new EvaluatorCollection(List.of(palindromes, emordnilap), List.of(fullPalindromes));

    // when
    Emordnilap result = collection.getWordEvaluatorOrThrow(Emordnilap.class);

    // then
    assertThat(result, sameInstance(emordnilap));
  }

  @Test
  void shouldReturnEvaluatorMatchingPredicate() {
    // given
    VowelCount vowelCount = mock(VowelCount.class);
    given(vowelCount.getLetterType()).willReturn(LetterType.VOWELS);
    VowelCount consonantCount = mock(VowelCount.class);
    given(consonantCount.getLetterType()).willReturn(LetterType.CONSONANTS);
    Palindromes palindromes = mock(Palindromes.class);
    FullPalindromes fullPalindromes = mock(FullPalindromes.class);
    EvaluatorCollection collection = new EvaluatorCollection(
        List.of(palindromes, vowelCount, consonantCount), List.of(fullPalindromes));

    // when
    VowelCount result = collection.getWordEvaluatorOrThrow(VowelCount.class,
        vc -> vc.getLetterType() == LetterType.CONSONANTS);

    // then
    assertThat(result, sameInstance(consonantCount));
  }

  @Test
  void shouldThrowForMultipleMatches() {
    // given
    VowelCount vowelCount = mock(VowelCount.class);
    VowelCount consonantCount = mock(VowelCount.class);
    Palindromes palindromes = mock(Palindromes.class);
    FullPalindromes fullPalindromes = mock(FullPalindromes.class);
    EvaluatorCollection collection = new EvaluatorCollection(
        List.of(palindromes, vowelCount, consonantCount), List.of(fullPalindromes));

    // when
    IllegalStateException ex = assertThrows(IllegalStateException.class,
        () -> collection.getWordEvaluatorOrThrow(VowelCount.class));

    // then
    assertThat(ex.getMessage(), equalTo("Found 2 evaluators of type VowelCount but expected only 1"));
  }

  @Test
  void shouldThrowForZeroMatches() {
    // given
    VowelCount vowelCount = mock(VowelCount.class);
    VowelCount consonantCount = mock(VowelCount.class);
    Palindromes palindromes = mock(Palindromes.class);
    FullPalindromes fullPalindromes = mock(FullPalindromes.class);
    EvaluatorCollection collection = new EvaluatorCollection(
        List.of(palindromes, vowelCount, consonantCount), List.of(fullPalindromes));

    // when
    IllegalStateException ex = assertThrows(IllegalStateException.class,
        () -> collection.getWordEvaluatorOrThrow(Emordnilap.class));

    // then
    assertThat(ex.getMessage(), equalTo("Found no matching evaluator of type Emordnilap"));
  }
}