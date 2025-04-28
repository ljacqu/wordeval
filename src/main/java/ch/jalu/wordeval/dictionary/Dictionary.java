package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.language.Language;
import lombok.Getter;
import lombok.ToString;

/**
 * Dictionary. Stores the {@link #file location} and other metadata.
 * <p>
 * Dictionaries are defined in {@link ch.jalu.wordeval.appdata.DictionaryData}.
 *
 * @see HunspellDictionary
 */
@Getter
@ToString(of = "identifier")
public abstract class Dictionary {

  /**
   * The dictionary identifier is typically the ISO-639-1 abbreviation of its language.
   */
  private final String identifier;

  private final String file;

  private final Language language;

  protected Dictionary(String identifier, String file, Language language) {
    this.identifier = identifier;
    this.file = file;
    this.language = language;
  }
}
