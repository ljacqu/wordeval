package ch.jalu.wordeval.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

/**
 * Runnable Spring boot application that is self-contained. Command-line tools that need the <i>wordeval</i>
 * Spring context can extend this class in order to benefit from dependency injection etc. without any
 * boilerplate code besides defining a main method calling {@link #runApplication}.
 * <p>
 * Extending classes should not be annotated with any Spring annotation so that they are not picked up
 * by the general Spring configuration, which would cause this runner to be run elsewhere.
 * This approach avoids the need of defining additional configuration classes or further nesting.
 * The extending class's logic is simply implemented in {@link CommandLineRunner#run}.
 */
public abstract class SpringContainedRunner implements CommandLineRunner {

  /**
   * Starts a Spring application with the base configuration and the supplied class as additional bean.
   * The provided class should not be annotated with any Spring annotations so that the class is not
   * picked up by the base config (see class javadoc).
   * <p>
   * The main method of all extending classes should be a call to this method.
   *
   * @param runnerClass {@code this} class extending {@link SpringContainedRunner}
   * @param args command-line arguments
   */
  protected static void runApplication(Class<?> runnerClass, String... args) {
    Class<?>[] sources = {BaseConfiguration.class, runnerClass};
    SpringApplication.run(sources, args);
  }
}
