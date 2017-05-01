package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import com.google.common.collect.Multimap;
import org.junit.Test;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link DiacriticHomonyms}.
 */
public class DiacriticHomonymsTest {
  
  private static final Language language = newLanguage("zxx").build();
  
  @Test
  public void shouldFindDiacriticHomonyms() {
    String[] words = { "schön", "schon", "sûr", "sur", "ça", "çà", "des", "dés", "dès", "le", "la", "là" };
    DiacriticHomonyms evaluator = new DiacriticHomonyms(language.getLocale());
    
    for (String word : words) {
      evaluator.processWord(Alphabet.LATIN.removeAccents(word), word);
    }
    Multimap<String, String> results = evaluator.getResults();
    
    assertThat(results.keySet(), containsInAnyOrder("schon", "sur", "ca", "des", "le", "la"));
    assertThat(results.get("le"), contains("le"));
    assertThat(results.get("sur"), containsInAnyOrder("sur", "sûr"));
    assertThat(results.get("ca"), containsInAnyOrder("ça", "çà"));
    assertThat(results.get("schon"), containsInAnyOrder("schon", "schön"));
  }

}
