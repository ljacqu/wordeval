package ch.ljacqu.wordeval.evaluation;

import static ch.ljacqu.wordeval.TestUtil.asSet;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;

public class EvaluatorServiceTest {
  
  @Test
  public void shouldGetPostEvaluators() {
    Evaluator<?> e1 = new Evaluator1();
    Evaluator<?> e2 = new Evaluator2();
    Evaluator<?> e3 = new Evaluator3();
    List<Evaluator<?>> evaluators = Arrays.asList(e1, e2, e3);
    
    Map<Evaluator<?>, Evaluator<?>> postEvaluators = EvaluatorService.getPostEvaluators(evaluators);
    
    assertThat(postEvaluators, aMapWithSize(2));
    assertThat(postEvaluators.get(e2), equalTo(e1));
    assertThat(postEvaluators.get(e3), equalTo(e1));
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfNoParameterMatchPossible() {
    Evaluator<?> e1 = new Evaluator1();
    Evaluator<?> e2 = new SimpleEvaluator() {
      @PostEvaluator
      public void postEval(Evaluator3 e) {
      }
    };
    
    EvaluatorService.getPostEvaluators(Arrays.asList(e1, e2));
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionForInvalidPostEvaluatorMethods() {
    Evaluator<?> e1 = new SimpleEvaluator() {
      @PostEvaluator
      public void postEval(Evaluator<?> e, String a) {
      }
    };
    
    EvaluatorService.getPostEvaluators(Arrays.asList(e1));
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionForPostEvaluatorMethodWithNoParams() {
    Evaluator<?> e1 = new SimpleEvaluator() {
      @PostEvaluator
      public void emptyPostEval() {
      }
    };
    
    EvaluatorService.getPostEvaluators(Arrays.asList(e1));
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionForPostEvaluatorMethodWithWrongParam() {
    Evaluator<?> e1 = new SimpleEvaluator() {
      @PostEvaluator
      public void emptyPostEval(String word) {
      }
    };
    
    EvaluatorService.getPostEvaluators(Arrays.asList(e1));
  }
  
  @Test
  public void shouldExecutePostEvaluatorMethods() {
    Evaluator1 e1 = Mockito.mock(Evaluator1.class);
    Map<String, Set<String>> results = new HashMap<>();
    results.put("test", asSet("test", "testing", "tester"));
    when(e1.getResults()).thenReturn(results);
    
    Evaluator2 e2 = new Evaluator2();
    
    Map<Evaluator<?>, Evaluator<?>> postEvaluators = new HashMap<>();
    postEvaluators.put(e2, e1);
    EvaluatorService.executePostEvaluators(postEvaluators);
    assertTrue(e2.postEvaluatorCalled);
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionForInvalidPostEvaluator() {
    Evaluator1 e1 = new Evaluator1();
    Map<Evaluator<?>, Evaluator<?>> postEvaluators = new HashMap<>();
    postEvaluators.put(e1, e1);
    
    EvaluatorService.executePostEvaluators(postEvaluators);
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionForInvalidPostEvaluator2() {
    Evaluator1 e1 = new Evaluator1();
    SimpleEvaluator e2 = new SimpleEvaluator() {
      @PostEvaluator
      public void process(String w) {
      }
    };
    Map<Evaluator<?>, Evaluator<?>> postEvaluators = new HashMap<>();
    postEvaluators.put(e2, e1);
    
    EvaluatorService.executePostEvaluators(postEvaluators);
  }
  
  
  
  // ----------
  
  private static class Evaluator1 extends SimpleEvaluator {
  }
  
  private static class Evaluator2 extends PartWordEvaluator {
    boolean postEvaluatorCalled = false;
    
    @PostEvaluator
    public void postEvalMethod(Evaluator1 e) {
      assertThat(e.getResults(), not(anEmptyMap()));
      postEvaluatorCalled = true;
    }
    
    @Override
    public void processWord(String a, String b) {
    }
  }
  
  private static class Evaluator3 extends SimpleEvaluator {
    boolean postEvaluatorCalled = false;
    
    @PostEvaluator
    public void m(Evaluator1 e) {
      postEvaluatorCalled = true;
    }
  }
  
  private static abstract class SimpleEvaluator extends PartWordEvaluator {
    @Override
    public void processWord(String a, String b) {
      addEntry(a, b);
    }
  }

}
