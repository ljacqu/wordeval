package ch.ljacqu.wordeval.evaluation;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;

import org.junit.Test;

import ch.ljacqu.wordeval.language.Alphabet;
import ch.ljacqu.wordeval.language.Language;
import ch.ljacqu.wordeval.language.LanguageService;

public class DiacriticHomonymsTest {
  
  private static Language language = new Language("zxx", Alphabet.LATIN);
  
  @Test
  public void shouldFindDiacriticHomonyms() {
    String[] words = { "schön", "schon", "sûr", "sur", "ça", "çà", "des", "dés", "dès", "le", "la", "là" };
    DiacriticHomonyms evaluator = new DiacriticHomonyms(language);
    
    for (String word : words) {
      evaluator.processWord(LanguageService.removeAccentsFromWord(word, Alphabet.LATIN), word);
    }
    Map<String, Set<String>> results = evaluator.getResults();
    
    assertThat(results.keySet(), containsInAnyOrder("schon", "sur", "ca", "des", "le", "la"));
    assertThat(results.get("le"), contains("le"));
    assertThat(results.get("sur"), containsInAnyOrder("sur", "sûr"));
    assertThat(results.get("ca"), containsInAnyOrder("ça", "çà"));
    assertThat(results.get("schon"), containsInAnyOrder("schon", "schön"));
  }

}
