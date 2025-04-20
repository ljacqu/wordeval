package ch.jalu.wordeval;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to rename the dictionaries from the Hunspell
 * repository at https://github.com/titoBouzout/Dictionaries
 * to the language code of the dictionary, as used in <i>wordeval</i>.
 */
@Slf4j
public class DictionaryRenamer {
  
  private static final File DICT_DIRECTORY = new File("./dict");
  private static final Set<String> USE_EXTENSIONS = Set.of("aff", "dic", "txt");
  private static final Map<String, String> REPLACEMENTS = initReplacements();
  
  private DictionaryRenamer() {
  }

  /**
   * Scans the /dict folder and renames files to the codes.
   * @param args .
   */
  public static void main(String[] args) {
    if (!DICT_DIRECTORY.exists()) {
      throw new IllegalStateException("Directory '" + DICT_DIRECTORY + "' does not exist");
    }

    File[] files = DICT_DIRECTORY.listFiles();
    if (files == null) {
      throw new IllegalStateException("Could not read files from dictionary " + DICT_DIRECTORY);
    }
    Arrays.stream(files)
      .filter(file -> USE_EXTENSIONS.contains(Files.getFileExtension(file.getName())))
      .forEach(file -> applyReplacement(file));
    
    log.info("End renaming files");
  }
  
  private static void applyReplacement(File f) {
    String fileName = Files.getNameWithoutExtension(f.getName()).replace("%20", " ");

    if (!REPLACEMENTS.containsKey(fileName)) {
      log.info("No replacement for '{}'", fileName);
      return;
    }
    
    String newName = fileName.replace(fileName, REPLACEMENTS.get(fileName))
        + "." + Files.getFileExtension(f.getName());
    File newFile = new File(DICT_DIRECTORY + File.separator + newName);
    if (newFile.exists() && !newFile.isDirectory()) {
      log.warn("Not renaming '{}' to '{}': file with such name already exists", fileName, newName);
    } else {
      boolean couldRename = f.renameTo(newFile);
      if (couldRename) {
        log.info("Renamed '{}' to '{}'", fileName, newName);
      } else {
        log.warn("Could not rename '{}' to '{}'", fileName, newName);
      }
    }
  }
  
  private static Map<String, String> initReplacements() {
    return ImmutableMap.<String, String>builder()
    .put("Basque", "eu")
    .put("Bulgarian", "bg")
    .put("Catalan", "ca")
    .put("Croatian", "hr")
    .put("Czech", "cs")
    .put("Danish", "da")
    .put("Dutch", "nl")
    .put("English (American)", "en-us")
    .put("English (Australian)", "en-au")
    .put("English (British)", "en-uk")
    .put("English (Canadian)", "en-ca")
    .put("Estonian", "et")
    .put("Finnish", "fi")
    .put("French", "fr")
    .put("Galego", "gl")
    .put("German", "de")
    .put("German_de_AT", "de-at")
    .put("German_de_CH", "de-ch")
    .put("German_de_DE", "de-de")
    .put("Greek", "el")
    .put("Hungarian", "hu")
    .put("Italian", "it")
    .put("Lithuanian", "lt")
    .put("Luxembourgish", "lb")
    .put("Mongolian", "mn")
    .put("Norwegian (Bokmal)", "nb")
    .put("Norwegian (Nynorsk)", "nn")
    .put("Polish", "pl")
    .put("Portuguese (Brazilian)", "pt-br")
    .put("Portuguese (European)", "pt-pt")
    .put("Romanian", "ro")
    .put("Romanian (Modern)", "ro")
    .put("Russian", "ru")
    .put("Serbian (Cyrillic)", "sr-cyrl")
    .put("Serbian (Latin)", "sr-latn")
    .put("Slovak_sk_SK", "sk")
    .put("Slovenian", "sl")
    .put("Spanish", "es")
    .put("Swedish", "sv")
    .put("Turkish", "tr")
    .put("Ukrainian_uk_UA", "uk")
    .put("Vietnamese_vi_VN", "vi")
    .build();
  }
  
}
