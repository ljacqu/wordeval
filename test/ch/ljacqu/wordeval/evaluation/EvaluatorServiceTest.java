package ch.ljacqu.wordeval.evaluation;

import static ch.ljacqu.wordeval.TestUtil.asSet;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;

import lombok.Getter;

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
        // --
      }
    };
    
    EvaluatorService.getPostEvaluators(Arrays.asList(e1, e2));
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionForInvalidPostEvaluatorMethods() {
    Evaluator<?> e1 = new SimpleEvaluator() {
      @PostEvaluator
      public void postEval(Evaluator<?> e, String a) {
        throw new UnsupportedOperationException("Method should not have been invoked");
      }
    };
    
    EvaluatorService.getPostEvaluators(Arrays.asList(e1));
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionForPostEvaluatorMethodWithNoParams() {
    Evaluator<?> e1 = new SimpleEvaluator() {
      @PostEvaluator
      public void emptyPostEval() {
        throw new UnsupportedOperationException("Method should not have been invoked");
      }
    };
    
    EvaluatorService.getPostEvaluators(Arrays.asList(e1));
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionForPostEvaluatorMethodWithWrongParam() {
    Evaluator<?> e1 = new SimpleEvaluator() {
      @PostEvaluator
      public void emptyPostEval(String word) {
        throw new UnsupportedOperationException("Method should not have been invoked");
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
  
  @Test
  public void shouldAllowSubTypes() {
    Evaluator1 e1 = new Evaluator1();
    SimpleEvaluator e2 = new SimpleEvaluator() {
      @PostEvaluator
      public void process(SimpleEvaluator simpleEv) {
        // --
      }
    };
    
    Map<Evaluator<?>, Evaluator<?>> postEvaluators = 
        EvaluatorService.getPostEvaluators(Arrays.asList(e1, e2));
    
    assertThat(postEvaluators, aMapWithSize(1));
    assertThat(postEvaluators, hasKey(e2));
    assertThat(postEvaluators.get(e2), equalTo(e1));
  }
  
  @Test
  public void shouldGetBaseWithBaseMatcherMethod() {
    SimpleEvaluator postEvaluator = new SimpleEvaluator() {
      @PostEvaluator
      public void postEvaluate(EvaluatorWithParam ev) {
        // --
      }
      @BaseMatcher
      public boolean isMatch(EvaluatorWithParam ev) {
        return ev.getIndexParam() == 3;
      }
    };
    List<Evaluator<?>> evaluators = Arrays.asList(
        postEvaluator, new EvaluatorWithParam(1), new EvaluatorWithParam(3));
    
    Map<Evaluator<?>, Evaluator<?>> postEvaluators = EvaluatorService.getPostEvaluators(evaluators);
    
    assertThat(postEvaluators, aMapWithSize(1));
    assertTrue(postEvaluators.containsKey(postEvaluator));
    Evaluator<?> baseEvaluator = postEvaluators.get(postEvaluator);
    assertThat(baseEvaluator, instanceOf(EvaluatorWithParam.class));
    assertThat(((EvaluatorWithParam) baseEvaluator).getIndexParam(), equalTo(3));
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowIfThereIsBaseMatcherButNotPostEvaluator() {
    Evaluator1 base = new Evaluator1();
    Evaluator1 postEvaluator = new Evaluator1() {
      @BaseMatcher
      public boolean isMatch(Evaluator1 ev) {
        return true;
      }
    };

    EvaluatorService.getPostEvaluators(Arrays.asList(base, postEvaluator));
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowIfBaseMatcherDoesNotReturnBoolean() {
    Evaluator1 base1 = new Evaluator1();
    SimpleEvaluator postEvaluator = new SimpleEvaluator() {
      @PostEvaluator
      public void process(Evaluator1 base) {
        // --
      }
      @BaseMatcher
      public int isMatch(Evaluator1 base) {
        return 5;
      }
    };
    
    EvaluatorService.getPostEvaluators(Arrays.asList(base1, postEvaluator));
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowIfBaseMatcherAndPostEvaluatorDoNotMatch() {
    Evaluator1 base1 = new Evaluator1();
    EvaluatorWithParam base2 = new EvaluatorWithParam(14);
    SimpleEvaluator postEvaluator = new SimpleEvaluator() {
      @PostEvaluator
      public void process(Evaluator1 base) {
        // --
      }
      @BaseMatcher
      public boolean isMatch(EvaluatorWithParam ewp) {
        return true;
      }
    };
    
    EvaluatorService.getPostEvaluators(Arrays.asList(base1, base2, postEvaluator));
  }
  
  @Test
  public void shouldAllowBaseMatcherWithBooleanClassReturnType() {
    EvaluatorWithParam base1 = new EvaluatorWithParam(44);
    EvaluatorWithParam base2 = new EvaluatorWithParam(31);
    SimpleEvaluator postEvaluator = new SimpleEvaluator() {
      @PostEvaluator
      public void postProcess(EvaluatorWithParam ev) {
        // --
      }
      @BaseMatcher
      public Boolean isMatch(EvaluatorWithParam ev) {
        return ev.getIndexParam() % 2 == 1;
      }
    };
    
    Map<Evaluator<?>, Evaluator<?>> postEvaluators = 
        EvaluatorService.getPostEvaluators(Arrays.asList(base1, base2, postEvaluator));
    
    assertThat(postEvaluators, aMapWithSize(1));
    assertThat(postEvaluators, hasKey(postEvaluator));
    assertThat(postEvaluators.get(postEvaluator), equalTo(base2));
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
      // --
    }
  }
  
  private static class Evaluator3 extends SimpleEvaluator {
    boolean postEvaluatorCalled = false;
    
    @PostEvaluator
    public void m(Evaluator1 e) {
      postEvaluatorCalled = true;
    }
  }
  
  private static class EvaluatorWithParam extends SimpleEvaluator {
    @Getter
    private int indexParam;
    public EvaluatorWithParam(int i) {
      indexParam = i;
    }
  }
  
  private abstract static class SimpleEvaluator extends PartWordEvaluator {
    @Override
    public void processWord(String a, String b) {
      addEntry(a, b);
    }
  }

}
