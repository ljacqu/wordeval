package ch.jalu.wordeval;

import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.config.SpringContainedRunner;
import ch.jalu.wordeval.dictionary.Dictionary;
import ch.jalu.wordeval.dictionary.HunspellDictionary;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.MoreFiles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class to rename the dictionaries from the Hunspell
 * repository at https://github.com/titoBouzout/Dictionaries
 * to the language code of the dictionary, as used in <i>wordeval</i>.
 */
@Slf4j
public class DictionaryRenamer extends SpringContainedRunner {
  
  private static final Path DICT_DIRECTORY = Paths.get("./dict");
  private static final Set<String> USE_EXTENSIONS = Set.of("aff", "dic", "txt");
  private static final Map<String, String> REPLACEMENTS = initReplacements();

  private Set<String> knownDictionaryCodes;

  @Autowired
  private DataUtils dataUtils;

  @Autowired
  private AppData appData;
  
  private DictionaryRenamer() {
  }

  public static void main(String[] args) {
    runApplication(DictionaryRenamer.class, args);
  }

  /**
   * Scans the /dict folder and renames files to the codes.
   *
   * @param args .
   */
  @Override
  public void run(String... args) throws IOException {
    knownDictionaryCodes = appData.getAllDictionaries().stream()
        .map(Dictionary::getIdentifier)
        .collect(Collectors.toUnmodifiableSet());

    if (!Files.exists(DICT_DIRECTORY)) {
      throw new IllegalStateException("Directory '" + DICT_DIRECTORY + "' does not exist");
    }

    try (Stream<Path> filesStream = Files.list(DICT_DIRECTORY)) {
      filesStream
          .filter(file -> USE_EXTENSIONS.contains(MoreFiles.getFileExtension(file)))
          .forEach(this::applyReplacement);
    }
    
    log.info("End renaming files");

    sanitizeHungarianAffixFileIfNeeded();
  }
  
  private void applyReplacement(Path f) {
    String fileName = MoreFiles.getNameWithoutExtension(f).replace("%20", " ");

    if (!REPLACEMENTS.containsKey(fileName)) {
      if (!knownDictionaryCodes.contains(fileName)) {
        // Log only if the file cannot be renamed and is not named like a dictionary code
        log.info("No replacement for '{}'", fileName);
      }
      return;
    }

    String newName = fileName.replace(fileName, REPLACEMENTS.get(fileName)) + "." + MoreFiles.getFileExtension(f);
    Path newFile = DICT_DIRECTORY.resolve(newName);

    if (Files.exists(newFile)) {
      log.warn("Not renaming '{}' to '{}': file with such name already exists", fileName, newName);
    } else {
      dataUtils.move(f, newFile);
      log.info("Renamed '{}' to '{}'", fileName, newName);
    }
  }

  // The hungarian affix file from https://github.com/wooorm/dictionaries/tree/main/dictionaries/ seems to have
  // HTML entities in it.
  private void sanitizeHungarianAffixFileIfNeeded() {
    String hungarianAffixFile = ((HunspellDictionary) appData.getDictionary("hu")).getAffixFile();
    if (Files.exists(Paths.get(hungarianAffixFile))) {
      List<String> contents = dataUtils.readAllLines(hungarianAffixFile);
      if (contents.stream().anyMatch(line -> line.contains("&agrave;"))) {
        // Inexplicably, the source for hu.aff has some HTML entities that the repo also doesn't know what to do about,
        // so we have to replace them for now. Some of the regexp patterns are not valid otherwise.
        // Additionally, some patterns are like this: [áéiíóőuúůüűhy-&agrave;&ugrave;], which does not result in
        // a valid range. We assume that the hyphen is an error for now...
        // Some more HTML entities could be replaced, but they are only used in directives that wordeval ignores
        // (such as REP).
        String updatedContents = dataUtils.readFile(hungarianAffixFile)
            .replace("&agrave;", "à")
            .replace("&ugrave;", "ù")
            .replace("&Aring;", "Å")
            .replace("&aring;", "å")
            .replace("űy-àù", "űyàù")
            .replace("üű-àù", "üűàù")
            .replace("űhy-àù", "űhyàù");
        dataUtils.writeToFile(hungarianAffixFile, updatedContents);
        log.info("Cleaned up HTML entities in Hungarian affixes file, '{}'", hungarianAffixFile);
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
