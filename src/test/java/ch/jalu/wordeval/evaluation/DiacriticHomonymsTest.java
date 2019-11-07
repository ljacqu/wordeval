package ch.jalu.wordeval.evaluation;

import ch.jalu.wordeval.language.Alphabet;
import ch.jalu.wordeval.language.Language;
import ch.jalu.wordeval.language.LanguageService;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;

import static ch.jalu.wordeval.TestUtil.newLanguage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Test for {@link DiacriticHomonyms}.
 */
class DiacriticHomonymsTest {
  
  private static final Language language = newLanguage("zxx").build();
  
  @Test
  void shouldFindDiacriticHomonyms() {
    String[] words = { "schön", "schon", "sûr", "sur", "ça", "çà", "des", "dés", "dès", "le", "la", "là" };
    DiacriticHomonyms evaluator = new DiacriticHomonyms(language.getLocale());
    
    for (String word : words) {
      evaluator.processWord(LanguageService.removeAccentsFromWord(word, Alphabet.LATIN), word);
    }
    Multimap<String, String> results = evaluator.getResults();
    
    assertThat(results.keySet(), containsInAnyOrder("schon", "sur", "ca", "des", "le", "la"));
    assertThat(results.get("le"), contains("le"));
    assertThat(results.get("sur"), containsInAnyOrder("sur", "sûr"));
    assertThat(results.get("ca"), containsInAnyOrder("ça", "çà"));
    assertThat(results.get("schon"), containsInAnyOrder("schon", "schön"));
  }

}
