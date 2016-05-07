package ch.jalu.wordeval.evaluation;

import com.google.common.collect.TreeMultimap;
import lombok.Getter;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@SuppressWarnings("JavaDoc")
public class EvaluatorServiceTest {
  
  @Test
  public void shouldGetPostEvaluators() {
    Evaluator<?> e1 = new Evaluator1();
    Evaluator<?> e2 = new Evaluator2();
    Evaluator<?> e3 = new Evaluator3();
    List<Evaluator<?>> evaluators = Arrays.asList(e1, e2, e3);
    
    Map<PostEvaluator<?>, Evaluator<?>> postEvaluators = EvaluatorService.getPostEvaluators(evaluators);
    
    assertThat(postEvaluators, aMapWithSize(2));
    assertThat(postEvaluators.get(e2), equalTo(e1));
    assertThat(postEvaluators.get(e3), equalTo(e1));
  }
  
  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionIfNoParameterMatchPossible() {
    Evaluator<?> e1 = new Evaluator1();
    Evaluator<?> e2 = new SimplePostEvaluator<Evaluator3>() {
      @Override
      public void evaluateWith(Evaluator3 e) {
        // --
      }
      @Override public Class<Evaluator3> getType() { return Evaluator3.class; }
    };
    
    EvaluatorService.getPostEvaluators(Arrays.asList(e1, e2));
  }
  
  @Test
  public void shouldExecutePostEvaluatorMethods() {
    Evaluator1 e1 = Mockito.mock(Evaluator1.class);
    TreeMultimap<String, String> results = TreeMultimap.create();
    results.putAll("test", Arrays.asList("test", "testing", "tester"));
    when(e1.getResults()).thenReturn(results);
    
    Evaluator2 e2 = new Evaluator2();
    
    Map<PostEvaluator<?>, Evaluator<?>> postEvaluators = new HashMap<>();
    postEvaluators.put(e2, e1);
    EvaluatorService.executePostEvaluators(postEvaluators);
    assertTrue(e2.postEvaluatorCalled);
  }
  
  @Test
  public void shouldAllowSubTypes() {
    Evaluator1 e1 = new Evaluator1();
    SimpleEvaluator e2 = new SimplePostEvaluator<SimpleEvaluator>() {
      @Override
      public void evaluateWith(SimpleEvaluator simpleEv) {
        // --
      }
      @Override public Class<SimpleEvaluator> getType() { return SimpleEvaluator.class; }
    };
    
    Map<PostEvaluator<?>, Evaluator<?>> postEvaluators =
        EvaluatorService.getPostEvaluators(Arrays.asList(e1, e2));
    
    assertThat(postEvaluators, aMapWithSize(1));
    assertThat(postEvaluators, hasKey(e2));
    assertThat(postEvaluators.get(e2), equalTo(e1));
  }
  
  @Test
  public void shouldGetBaseWithBaseMatcherMethod() {
    SimpleEvaluator postEvaluator = new SimplePostEvaluator<EvaluatorWithParam>() {
      @Override
      public void evaluateWith(EvaluatorWithParam ev) {
        // --
      }
      @Override
      public boolean isMatch(EvaluatorWithParam ev) {
        return ev.getIndexParam() == 3;
      }
      @Override public Class<EvaluatorWithParam> getType() { return EvaluatorWithParam.class; }
    };
    List<Evaluator<?>> evaluators = Arrays.asList(
        postEvaluator, new EvaluatorWithParam(1), new EvaluatorWithParam(3));
    
    Map<PostEvaluator<?>, Evaluator<?>> postEvaluators = EvaluatorService.getPostEvaluators(evaluators);
    
    assertThat(postEvaluators, aMapWithSize(1));
    assertTrue(postEvaluators.containsKey(postEvaluator));
    Evaluator<?> baseEvaluator = postEvaluators.get(postEvaluator);
    assertThat(baseEvaluator, instanceOf(EvaluatorWithParam.class));
    assertThat(((EvaluatorWithParam) baseEvaluator).getIndexParam(), equalTo(3));
  }

  
  // ----------
  
  private static class Evaluator1 extends SimpleEvaluator {
  }
  
  private static class Evaluator2 extends PartWordEvaluator implements PostEvaluator<Evaluator1> {
    boolean postEvaluatorCalled = false;
    
    @Override
    public void evaluateWith(Evaluator1 e) {
      assertThat(e.getResults().size(), not(equalTo(0)));
      postEvaluatorCalled = true;
    }
    
    @Override
    public void processWord(String a, String b) {
      // --
    }
    @Override public Class<Evaluator1> getType() { return Evaluator1.class; }
  }
  
  private static class Evaluator3 extends SimpleEvaluator implements PostEvaluator<Evaluator1> {
    boolean postEvaluatorCalled = false;
    
    @Override
    public void evaluateWith(Evaluator1 e) {
      postEvaluatorCalled = true;
    }
    @Override public Class<Evaluator1> getType() { return Evaluator1.class; }
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

  private static abstract class SimplePostEvaluator<T extends Evaluator> extends SimpleEvaluator
      implements PostEvaluator<T> {
  }

}
