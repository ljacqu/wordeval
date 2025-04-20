package ch.jalu.wordeval.dictionary;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Word with all its word form types.
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "raw")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@ToString(of = "raw")
public class Word {

  /**
   * Word in its original form, as read from the dictionary.
   * <p>
   * Examples: cartagüeño (es), aujourd'hui (fr), Loftshøjgård (da)
   */
  private String raw;

  /**
   * Word in its locale-appropriate all lower case form (e.g. Turkish {@code I} becomes {@code ı}).
   * <p>
   * Examples: cartagüeño (es), aujourd'hui (fr), loftshøjgård (da)
   */
  private String lowercase;

  /**
   * Word all in lower case without any diacritics that aren't considered separate letters by the language in question.
   * <p>
   * Examples: cartagueño (es), aujourd'hui (fr), loftshøjgård (da)
   */
  private String withoutAccents;

  /**
   * Word all in lower case without any diacritics or any non-letters (like hyphens, apostrophes).
   * <p>
   * Examples: cartagueño (es), aujourdhui (fr), loftshøjgård (da)
   */
  private String withoutAccentsWordCharsOnly;

}
