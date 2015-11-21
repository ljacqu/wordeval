package ch.ljacqu.wordeval.evaluation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method in an evaluator which generates results based on another
 * evaluator. An evaluator may have one @PostEvaluator method, which must take
 * exactly one evaluator as argument. The <code>processWord(String, String)</code>
 * method is typically empty for evaluators using an @PostEvaluator method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostEvaluator {

}
