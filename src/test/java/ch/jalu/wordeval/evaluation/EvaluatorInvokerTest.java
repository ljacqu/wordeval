package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.dictionary.Word;
import ch.jalu.wordeval.dictionary.WordForm;
import ch.jalu.wordeval.evaluation.export.ExportObject;
import ch.jalu.wordeval.evaluation.export.ExportParams;
import ch.jalu.wordeval.language.LetterType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Test for {@link EvaluatorInvoker}.
 */
class EvaluatorInvokerTest {

  @Test
  void shouldInvokeAllDictionaryEvaluators() {
    // given
    DictionaryEvaluator de1 = mockDictionaryEvaluator();
    DictionaryEvaluator de2 = mockDictionaryEvaluator();
    DictionaryEvaluator de3 = mockDictionaryEvaluator();
    PostEvaluator pe1 = mock(PostEvaluator.class);
    PostEvaluator pe2 = mock(PostEvaluator.class);
    Word word = mock(Word.class);
    given(word.getForm(any(WordForm.class))).willReturn("word");

    // when
    EvaluatorInvoker invoker = new EvaluatorInvoker(Arrays.asList(de1, pe1, pe2, de2, de3));
    invoker.processWord(word);

    // then
    verify(de1).processWord("word", "word");
    verify(de2).processWord("word", "word");
    verify(de3).processWord("word", "word");
    verifyZeroInteractions(pe1);
    verifyZeroInteractions(pe2);
  }

  @Test
  void shouldInvokePostEvaluators() {
    // given
    DictEvaluator1 de1 = new DictEvaluator1();
    DictEvaluator3 de3 = new DictEvaluator3();
    DictEvalWithParam depVowel = new DictEvalWithParam(LetterType.VOWELS);
    DictEvalWithParam depCons  = new DictEvalWithParam(LetterType.CONSONANTS);

    PostEvaluator1 pe1 = spy(new PostEvaluator1());
    PostEvaluator2 pe2 = spy(new PostEvaluator2());
    PostEvalWithParam pepVowel = spy(new PostEvalWithParam(LetterType.VOWELS));
    PostEvalWithParam pepCons  = spy(new PostEvalWithParam(LetterType.CONSONANTS));

    EvaluatorInvoker invoker = new EvaluatorInvoker(
        Arrays.asList(de1, de3, depVowel, depCons, pe1, pe2, pepVowel, pepCons));

    // when
    invoker.executePostEvaluators();

    // then
    verify(pe1).evaluateWith(de1);
    verify(pe2).evaluateWith(de3);
    verify(pepVowel).evaluateWith(depVowel);
    verify(pepCons).evaluateWith(depCons);
  }

  @Test
  void shouldThrowForUnmatchedBase() {
    // given
    DictEvaluator1 de1 = new DictEvaluator1();
    PostEvaluator2 pe2 = new PostEvaluator2();
    EvaluatorInvoker invoker = new EvaluatorInvoker(Arrays.asList(de1, pe2));

    // when / then
    assertThrows(IllegalStateException.class, invoker::executePostEvaluators);
  }

  private static DictionaryEvaluator mockDictionaryEvaluator() {
    DictionaryEvaluator evaluator = mock(DictionaryEvaluator.class);
    given(evaluator.getWordForm()).willReturn(WordForm.RAW);
    return evaluator;
  }
  
  // ----------

  /** Sample dictionary evaluator 1. */
  private static final class DictEvaluator1 extends PartWordEvaluator {
    @Override
    public void processWord(String a, String b) {
      // noop
    }
  }

  /** Sample: dictionary evaluator 2. */
  private static class DictEvaluator2 extends WordStatEvaluator {
    @Override
    public void processWord(String a, String b) {
      // noop
    }
  }

  /** Sample: dictionary evaluator 3, child of dictEval 2. */
  private static final class DictEvaluator3 extends DictEvaluator2 {
  }

  /** Sample: dictionary evaluator with param. */
  @Getter
  @AllArgsConstructor
  private static final class DictEvalWithParam extends WordStatEvaluator {

    private LetterType letterType;

    @Override public void processWord(String a, String b) { }

  }

  /** Sample post evaluator 1. */
  private static class PostEvaluator1 extends PostEvaluatorAbstraction<Integer, DictEvaluator1> {
    PostEvaluator1() {
      super(DictEvaluator1.class);
    }
  }

  /** Sample post evaluator 2. */
  private static class PostEvaluator2 extends PostEvaluatorAbstraction<Boolean, DictEvaluator2> {
    PostEvaluator2() {
      super(DictEvaluator2.class);
    }
  }

  /** Sample: post evaluator with param that must match with base. */
  private static class PostEvalWithParam extends PostEvaluatorAbstraction<String, DictEvalWithParam> {

    private final LetterType letterType;

    PostEvalWithParam(LetterType letterType) {
      super(DictEvalWithParam.class);
      this.letterType = letterType;
    }

    @Override
    public boolean isMatch(DictEvalWithParam evaluator) {
      return letterType.equals(evaluator.getLetterType());
    }
  }

  @Getter
  private static abstract class PostEvaluatorAbstraction<K extends Comparable, B extends Evaluator>
      extends PostEvaluator<K, B> {

    private final Class<B> type;
    private boolean wasEvaluatorCalled;

    PostEvaluatorAbstraction(Class<B> type) {
      this.type = type;
    }

    @Override
    public void evaluateWith(B base) {
      wasEvaluatorCalled = true;
    }

    @Override
    public ExportObject toExportObject(String name, ExportParams params) {
      return null;
    }
  }

}
