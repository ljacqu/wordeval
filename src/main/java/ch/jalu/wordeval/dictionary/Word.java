package ch.jalu.wordeval.dictionary;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Word with all its word form types.
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Word {

  private String raw;
  private String lowercase;
  private String withoutAccents;
  private String withoutAccentsWordCharsOnly;

}
