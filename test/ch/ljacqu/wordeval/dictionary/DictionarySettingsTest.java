package ch.ljacqu.wordeval.dictionary;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import ch.ljacqu.wordeval.language.Alphabet;
import ch.ljacqu.wordeval.language.Language;

public class DictionarySettingsTest {

  // TODO: Create test with mocks to ensure that evaluators are always called

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionForUnknownDictionary() {
    DictionarySettings.get("non-existent");
  }
  
  @Test
  public void shouldAddAndRetrieveNewDictionarySettings() {
    DictionarySettings ds = DictionarySettings.add("zxx")
      .setSkipSequences("af")
      .setDelimiters('/', '#');
    
    DictionarySettings gotSettings = DictionarySettings.get(ds.getIdentifier());
    
    assertThat(gotSettings.getIdentifier(), equalTo(ds.getIdentifier()));
    assertThat(gotSettings.getDelimiters(), equalTo(ds.getDelimiters()));
    assertThat(gotSettings.getSkipSequences(), equalTo(ds.getSkipSequences()));
  }
  
  @Test
  public void shouldAddAndRetrieveCustomSettings() {
    DictionarySettings.add("zxx", TestSanitizer.class);
    
    DictionarySettings gotSettings = DictionarySettings.get("zxx");
    assertThat(gotSettings.getIdentifier(), equalTo("zxx"));
    assertThat(gotSettings.getSkipSequences(), nullValue());
    assertThat(gotSettings.getDelimiters(), nullValue());
    assertThat(gotSettings.buildSanitizer(null), instanceOf(TestSanitizer.class));
  }

  private static class TestSanitizer extends Sanitizer {
    public TestSanitizer() {
      super(lang(), settings());
    }
    private static DictionarySettings settings() {
      return new DictionarySettings("zxx");
    }
    private static Language lang() {
      return new Language("zxx", Alphabet.CYRILLIC);
    }
  }

}
