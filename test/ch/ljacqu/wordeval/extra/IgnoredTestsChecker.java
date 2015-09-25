package ch.ljacqu.wordeval.extra;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Scans the <code>.extra</code> test package and ensures that the tests which
 * are not @LightWeight are not being executed by default.
 */
@LightWeight
public class IgnoredTestsChecker {

  private static final File FOLDER = new File("test/ch/ljacqu/wordeval/extra");
  private static final String PACKAGE = "ch.ljacqu.wordeval.extra";

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void shouldAllBeIgnored() {
    List<Class> classes = getClasses();
    List<Method> badMethods = new ArrayList<>();
    for (final Class clazz : classes) {
      if (clazz.isAnnotationPresent(LightWeight.class)) {
        System.out.println("Class '" + clazz.getSimpleName() + "' skipped: has @LightWeight");
        continue;
      } else if (clazz.isAnnotationPresent(Ignore.class)) {
        continue;
      }

      for (final Method method : clazz.getMethods()) {
        if (method.isAnnotationPresent(Test.class) && !method.isAnnotationPresent(Ignore.class)) {
          if (method.isAnnotationPresent(LightWeight.class)) {
            System.out.println("Method '" + method.getName() + "' in '" + clazz.getSimpleName()
                + "' skipped: has @LightWeight");
          } else {
            badMethods.add(method);
          }
        }
      }
    }
    evaluateMethods(classes, badMethods);
  }

  private List<Class> getClasses() {
    List<Class> classes = new ArrayList<>();
    for (final File file : FOLDER.listFiles()) {
      if (file.isFile() && file.getName().endsWith(".java")) {
        try {
          String fileName = file.getName();
          String className = PACKAGE + "." + fileName.substring(0, fileName.length() - 5);
          classes.add(Class.forName(className));
        } catch (ClassNotFoundException e) {
          throw new IllegalArgumentException(e);
        }
      }
    }
    return classes;
  }

  private void evaluateMethods(List<Class> classes, List<Method> methods) {
    if (classes.isEmpty()) {
      fail("Error loading classes; class list is empty");
    }
    for (Method method : methods) {
      System.err.println(method.getDeclaringClass() + "#" + method.getName());
    }
    assertTrue(methods.isEmpty());
  }

}
