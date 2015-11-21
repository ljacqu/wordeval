package ch.ljacqu.wordeval.evaluation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a method in a post evaluator. It should take one argument of
 * (sub)type Evaluator and should match the argument in the
 * {@link PostEvaluator} method and return a boolean, indicating whether or not
 * the parameter can be mapped to the postEvaluator.
 * No class may define a BaseMatcher method without a PostEvaluator method, but
 * a PostEvaluator method may exist without a BaseMatcher.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseMatcher {

}
