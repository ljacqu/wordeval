package ch.jalu.wordeval.config;

import ch.jalu.wordeval.WordEvalRoot;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Base configuration for Spring.
 */
@Configuration
@ComponentScan(basePackageClasses = WordEvalRoot.class)
public class BaseConfiguration {
}
