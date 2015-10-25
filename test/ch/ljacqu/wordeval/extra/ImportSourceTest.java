package ch.ljacqu.wordeval.extra;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import lombok.AllArgsConstructor;

/**
 * Checks that no files contain an import statement with a general asterisk.
 */
@LightWeight
public class ImportSourceTest {
  
  private static final String[] FOLDERS = {"src/", "test/"};
  
  private static final Pattern IMPORT_PATTERN = Pattern.compile(".*import.*\\*.*");

  @Test
  public void sourceFolderShouldNotHaveGeneralImports() throws IOException {
    for (String folder : FOLDERS) {
      checkImportsInFolder(folder);
    }
  }
  
  private static void checkImportsInFolder(String folder) throws IOException {
    List<String> errors = new ArrayList<>();
    ImportCheckerVisitor visitor = new ImportCheckerVisitor(errors);
    Files.walkFileTree(Paths.get(folder), visitor);
    if (!errors.isEmpty()) {
      System.err.println(String.join("\n", errors));
      fail("Found errors (see console)");
    } else {
      System.out.println("No general imports found for folder '" + folder + "'");
    }
  }  
  
  @AllArgsConstructor
  private static final class ImportCheckerVisitor extends SimpleFileVisitor<Path> {
    private List<String> errors;
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      if (attrs.isRegularFile() && file.getFileName().toString().endsWith(".java")) {
        checkFileForImports(file, errors);
      }
      return FileVisitResult.CONTINUE;
    }
  }
  
  private static void checkFileForImports(Path file, List<String> errors) throws IOException {
    for (String line : Files.readAllLines(file)) {
      if (line.indexOf("class") > -1 && line.indexOf("{") > -1) {
        break;
      }
      if (IMPORT_PATTERN.matcher(line).matches()) {
        errors.add(file.getFileName() + ": " + line);
      }
    }
  }
  
}
