package ch.jalu.wordeval.dictionary;

import ch.jalu.wordeval.TestUtil;
import ch.jalu.wordeval.appdata.AppData;
import ch.jalu.wordeval.dictionary.sanitizer.HuSanitizer;
import ch.jalu.wordeval.runners.DictionaryProcessor;
import lombok.extern.log4j.Log4j2;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

/**
 * Test for the {@link HuSanitizer Hungarian dictionary} (which has custom sanitation).
 */
@Log4j2
public class HuSanitizerTest {

  private static Dictionary huDictionary;
  
  @BeforeClass
  public static void initData() {
    huDictionary = new AppData().getDictionary("hu");
  }

  @Test
  public void shouldFindTheGivenWords() {
    if (!TestUtil.doesDictionaryFileExist(huDictionary)) {
      log.warn("Skipping Hu sanitizer test because dictionary doesn't exist");
      assumeThat(true, equalTo(false));
    }

    // given / when
    Set<String> words = DictionaryProcessor.readAllWords(huDictionary).stream()
      .map(Word::getRaw)
      .collect(Collectors.toSet());

    // then
    // should receive the words between Roman numerals
    assertThat(words, hasItems("csomóan", "csomó", "háromnegyed", "harmad",
      "kilenced", "milliomod", "trilliomod"));
    // should receive the split second words in two-word entries
    assertThat(words, hasItems("alak", "kor", "közben", "módra", "szer", "vég",
      "vége", "végén", "végi", "vevő", "cél"));
    // should get other words that have special treatment
    assertThat(words, hasItems("csak", "azért", "úti", "fő", "is"));
  }
}
