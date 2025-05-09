package ch.jalu.wordeval;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * Wrapper for interaction with the file system and JSON operations.
 */
@Component
@NoArgsConstructor
public class DataUtils {
  
  @Getter
  @Setter
  private boolean jsonPrettyPrint;
  
  @Getter(lazy = true)
  private final Gson gson = createGson();
  
  /**
   * Writes the content to the given file.
   *
   * @param filename the name of the file to write to
   * @param content the content to write
   */
  public void writeToFile(String filename, String content) {
    try {
      Files.write(Paths.get(filename), content.getBytes());
    } catch (IOException e) {
      throw new UncheckedIOException("Could not write to file '" + filename + "'", e);
    }
  }
  
  /**
   * Reads a file's contents as UTF-8.
   *
   * @param filename the name of the file to read
   * @return the contents of the file
   */
  public String readFile(String filename) {
    try {
      return Files.readString(Paths.get(filename));
    } catch (IOException e) {
      throw new UncheckedIOException("Could not read file '" + filename + "'", e);
    }
  }

  /**
   * Reads all lines of a file as UTF-8.
   *
   * @param filename the name of the file to read
   * @return the file's contents by line
   */
  public List<String> readAllLines(String filename) {
    try {
      return Files.readAllLines(Paths.get(filename));
    } catch (IOException e) {
      throw new UncheckedIOException("Could not read from file '" + filename + "'", e);
    }
  }

  /**
   * Returns all lines of the file read as UTF-8 in a stream that must be closed.
   *
   * @param filename the name of the file to read
   * @return stream of the file's lines
   */
  public Stream<String> lines(String filename) {
    try {
      return Files.lines(Paths.get(filename));
    } catch (IOException e) {
      throw new UncheckedIOException("Could not read from file '" + filename + "'", e);
    }
  }

  /**
   * Renames the source file to the target file.
   *
   * @param source the source to rename
   * @param target the target with the name to rename to
   */
  public void move(Path source, Path target) {
    try {
      Files.move(source, target);
    } catch (IOException e) {
      throw new UncheckedIOException("Could not move file '" + source + "' to '" + target + "'", e);
    }
  }

  /**
   * Converts an object to its JSON representation.
   *
   * @param o the object to convert
   * @return the generated JSON
   */
  public String toJson(Object o) {
    return getGson().toJson(o);
  }

  /**
   * Deserializes JSON to the specified type.
   *
   * @param <T> the result type
   * @param json the JSON text to deserialize
   * @param typeOfT the type of the resulting object
   * @return resulting object
   */
  public <T> T fromJson(String json, Type typeOfT) {
    return getGson().fromJson(json, typeOfT);
  }
  
  private Gson createGson() {
    if (jsonPrettyPrint) {
      return new GsonBuilder().setPrettyPrinting().create();
    }
    return new Gson();
  }

}
