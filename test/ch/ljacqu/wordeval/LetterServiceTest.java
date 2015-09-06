package ch.ljacqu.wordeval;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LetterServiceTest {

  @Test
  public void shouldRemoveDiacritics() {
    String[] givenWords = { "křižáků", "nőstényét", "iš vėlyvojo Jų",
        "długo mogą trwać występy koreańskich że", "požiūriu" };
    String[] expected = { "krizaku", "nostenyet", "is velyvojo Ju",
        "dlugo moga trwac wystepy koreanskich ze", "poziuriu" };

    for (int i = 0; i < givenWords.length; ++i) {
      assertEquals(LetterService.removeAccentsFromWord(givenWords[i]),
          expected[i]);
    }
  }

}
