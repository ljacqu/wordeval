package ch.ljacqu.wordeval.extra;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Checks that source classes ending with "Service" have a private
 * constructor and only static methods.
 */
@LightWeight
@Log4j2
public class ServiceGeneralTest {
  
  private static final String SOURCE_FOLDER = "src/";
  
  private static List<Class<?>> services;
  
  @BeforeClass
  public static void findServices() {
    List<Class<?>> classes = new ArrayList<>();
    ServiceFileVisitor visitor = new ServiceFileVisitor(classes);
    try {
      Files.walkFileTree(Paths.get(SOURCE_FOLDER), visitor);
      services = classes;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Test
  public void servicesShouldBeFinal() {
    for (Class<?> service : services) {
      assertThat("Class " + service + " should be final", 
          Modifier.isFinal(service.getModifiers()), equalTo(true));
    }
  }
  
  @Test
  public void shouldHavePrivateConstructor() {
    for (Class<?> service : services) {
      assertThat(service + " should have only one constructor, namely the default one", 
          service.getDeclaredConstructors(), arrayWithSize(1));

      try {
        Constructor<?> constr = service.getDeclaredConstructor();
        assertTrue("Constructor for " + service + " should be private",
            Modifier.isPrivate(constr.getModifiers()));
        cannotInstantiate(constr);
      } catch (NoSuchMethodException e) {
        fail("Service " + service + " does not have a private standard constructor");
      }
    }
  }
  
  @Test
  public void shouldHaveStaticMethodsOnly() {
    for (Class<?> service : services) {
      Arrays.stream(service.getDeclaredMethods())
        .forEach(method -> {
          assertThat("Method " + method.getName() + " in class " + service + " should be static",
              Modifier.isStatic(method.getModifiers()), equalTo(true));
        });
    }
  }
  
  private static void cannotInstantiate(Constructor<?> constr) {
    try {
      constr.setAccessible(true);
      constr.newInstance();
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
  
  @AllArgsConstructor
  private static final class ServiceFileVisitor extends SimpleFileVisitor<Path> {
    private List<Class<?>> resultList;
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      if (attrs.isRegularFile() && file.getFileName().toString().endsWith("Service.java")) {
        String className = file.toString()
          .substring(SOURCE_FOLDER.length(), file.toString().length() - 5)
          .replace(File.separator, ".");
        try {
          log.info("Found service '{}'", className);
          resultList.add(Class.forName(className));
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }
      return FileVisitResult.CONTINUE;
    }
  }
  
}
