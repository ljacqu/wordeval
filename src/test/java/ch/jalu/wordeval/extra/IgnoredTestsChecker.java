package ch.jalu.wordeval.extra;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

import lombok.extern.log4j.Log4j2;

/**
 * Scans the <code>.extra</code> test package and ensures that the tests which
 * are not @LightWeight are not being executed by default.
 */
@LightWeight
@Log4j2
public class IgnoredTestsChecker {

  private static final File FOLDER = new File("test/ch/ljacqu/wordeval/extra");
  private static final String PACKAGE = "ch.ljacqu.wordeval.extra";

  
  @SuppressWarnings("rawtypes")
  @Test
  public void shouldAllBeIgnored() {
    List<Class> classes = getClasses();
    List<Method> badMethods = new ArrayList<>();
    for (final Class clazz : classes) {
      if (isValidElement(clazz)) {
        log.info("Class '{}' skipped", clazz.getSimpleName());
        continue;
      }
      
      for (final Method method : clazz.getMethods()) {
        if (method.isAnnotationPresent(Test.class)) {
          if (isValidElement(method)) {
            log.info("Method {}#{} skipped", clazz.getSimpleName(), method.getName());
          } else {
            badMethods.add(method);
          }
        }
      }
    }
    evaluateMethods(classes, badMethods);
  }

  private static List<Class> getClasses() {
    List<Class> classes = new ArrayList<>();
    for (final File file : FOLDER.listFiles()) {
      if (file.isFile() && file.getName().endsWith(".java")) {
        try {
          String fileName = file.getName();
          String className = fileName.substring(0, fileName.length() - 5);
          classes.add(Class.forName(PACKAGE + "." + className));
        } catch (ClassNotFoundException e) {
          throw new IllegalArgumentException(e);
        }
      }
    }
    return classes;
  }

  private static void evaluateMethods(List<Class> classes, List<Method> methods) {
    if (classes.isEmpty()) {
      fail("Error loading classes; class list is empty");
    }
    for (Method method : methods) {
      log.error("Missing @Lightweight or @Ignore in {}#{}", method.getDeclaringClass().getName(), method.getName());
    }
    assertTrue(methods.isEmpty());
  }
  
  private static boolean isValidElement(AnnotatedElement member) {
    return member.isAnnotationPresent(LightWeight.class) || member.isAnnotationPresent(Ignore.class);
  }

}