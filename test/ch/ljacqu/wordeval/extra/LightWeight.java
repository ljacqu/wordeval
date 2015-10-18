package ch.ljacqu.wordeval.extra;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes light-weight tests in the extra package which can always be run and
 * which should be exempt from the check by {@link IgnoredTestsChecker}.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@interface LightWeight {

}
