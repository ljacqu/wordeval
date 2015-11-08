package ch.ljacqu.wordeval;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Utility class to rename the dictionaries from the Hunspell
 * repository at https://github.com/titoBouzout/Dictionaries
 * to the language code of the dictionary, as used in <i>wordeval</i>.
 */
@Log4j2
public class DictionaryRenamer {
  
  private static final File DICT_DIRECTORY = new File("./dict");
  private static final String[] USE_EXTENSIONS = { ".aff", ".dic", ".txt" };
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
    
    Arrays.stream(DICT_DIRECTORY.listFiles())
      .filter(file -> ArrayUtils.contains(USE_EXTENSIONS, getExtension(file)))
      .forEach(file -> applyReplacement(file));
    
    log.info("End renaming files");
  }
  
  private static String getExtension(File f) {
    int lastIndex = f.getName().lastIndexOf('.');
    return lastIndex > -1 ? f.getName().substring(lastIndex) : null;
  }
  
  private static String getFileName(File f) {
    int lastIndex = f.getName().lastIndexOf('.');    
    return lastIndex > -1 
        ? f.getName().substring(0, lastIndex)
        : f.getName();
  }
  
  private static void applyReplacement(File f) {
    String fileName = getFileName(f);
    if (!REPLACEMENTS.containsKey(fileName)) {
      log.info("No replacement for '{}'", fileName);
      return;
    }
    
    String newName = fileName.replace(fileName, REPLACEMENTS.get(fileName))
        + getExtension(f);
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
    return ReplacementBuilder.init()
    .add("Basque", "eu")
    .add("Bulgarian", "bg")
    .add("Catalan", "ca")
    .add("Croatian", "hr")
    .add("Czech", "cs")
    .add("Danish", "da")
    .add("Dutch", "nl")
    .add("English (American)", "en-us")
    .add("English (Australian)", "en-au")
    .add("English (British)", "en-uk")
    .add("English (Canadian)", "en-ca")
    .add("Estonian", "et")
    .add("French", "fr")
    .add("Galego", "gl")
    // TODO #48: Add other German variants
    .add("German", "de")
    .add("Greek", "el")
    .add("Hungarian", "hu")
    .add("Italian", "it")
    .add("Lithuanian", "lt")
    .add("Luxembourgish", "lb")
    .add("Mongolian", "mn")
    .add("Norwegian (Bokmal)", "nb")
    .add("Norwegian (Nynorsk)", "nn")
    .add("Polish", "pl")
    .add("Portuguese (Brazilian)", "pt-br")
    .add("Portuguese (European)", "pt-pt")
    .add("Romanian", "ro")
    .add("Romanian (Modern)", "ro")
    .add("Russian", "ru")
    .add("Serbian (Cyrillic)", "sr-cyrl")
    .add("Serbian (Latin)", "sr-latn")
    .add("Slovak_sk_SK", "sk")
    .add("Slovenian", "sl")
    .add("Spanish", "es")
    .add("Swedish", "sv")
    .add("Turkish", "tr")
    .add("Ukrainian_uk_UA", "uk")
    .add("Vietnamese_vi_VN", "vi")
    .getMap();
  }
  
  private static final class ReplacementBuilder {
    @Getter
    private Map<String, String> map;
    
    private ReplacementBuilder() {
      map = new HashMap<>();
    }
    
    public static ReplacementBuilder init() {
      return new ReplacementBuilder();
    }
    
    public ReplacementBuilder add(String name, String code) {
      map.put(name, code);
      return this;
    }
  }
  
}
