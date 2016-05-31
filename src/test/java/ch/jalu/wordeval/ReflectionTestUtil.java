package ch.jalu.wordeval;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Class for Reflection utils in tests.
 */
public final class ReflectionTestUtil {

  private ReflectionTestUtil() {
  }

  /**
   * Retrieves the fields of the given instance by reflection.
   *
   * @param clz the class of the instance
   * @param instance the instance to retrieve the field from, or null for static fields
   * @param fieldName the name of the field
   * @param <T> the type of the instance
   * @return the value of the given field
   */
  public static <T> Object getField(Class<T> clz, T instance, String fieldName) {
    Field field = getAccessible(() -> clz.getDeclaredField(fieldName));
    return performReflectionAction(() -> field.get(instance));
  }

  /**
   * Sets the field value of an object or class via reflection.
   *
   * @param <T> the type of the class
   * @param clz the class
   * @param instance the instance to set the value for, or null for static fields
   * @param fieldName the name of the field to set
   * @param value the value to set to the field
   */
  public static <T> void setField(Class<T> clz, T instance, String fieldName, Object value) {
    Field field = getAccessible(() -> clz.getDeclaredField(fieldName));
    performReflectionAction(() -> field.set(instance, value));
  }

  /**
   * Retrieves the method from a class with the given name and parameters.
   *
   * @param clz the class to retrieve the method from
   * @param methodName the name of the method
   * @param params the parameter types the method takes
   * @param <T> the type of the class
   * @return the retrieved Method object
   */
  public static <T> Method getMethod(Class<T> clz, String methodName, Class<?>... params) {
    return getAccessible(() -> clz.getDeclaredMethod(methodName, params));
  }

  /**
   * Invokes a method on the given instance with the provided parameters.
   *
   * @param method the method to invoke
   * @param instance the instance to invoke the method on
   * @param params the parameters to invoke the method with
   * @return the return value of the method
   */
  public static Object invokeMethod(Method method, Object instance, Object... params) {
    return performReflectionAction(() -> method.invoke(instance, params));
  }

  private static void performReflectionAction(CheckedRunnable consumer) {
    try {
      consumer.run();
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  private static <T> T performReflectionAction(CheckedSupplier<T> supplier) {
    try {
      return supplier.get();
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  private static <T extends AccessibleObject> T getAccessible(CheckedSupplier<T> supplier) {
    try {
      T accessibleObject = supplier.get();
      accessibleObject.setAccessible(true);
      return accessibleObject;
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  @FunctionalInterface
  private interface CheckedSupplier<T> {
    T get() throws ReflectiveOperationException;
  }

  @FunctionalInterface
  private interface CheckedRunnable {
    void run() throws ReflectiveOperationException;
  }
}
