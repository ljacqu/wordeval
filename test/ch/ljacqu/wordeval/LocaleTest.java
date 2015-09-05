package ch.ljacqu.wordeval;

import static org.junit.Assert.assertEquals;
import java.util.Locale;
import org.junit.Test;

public class LocaleTest {

  @Test
  public void shouldLowerCaseAccentedWordsProperly() {
    String text = "AÇ Êè OČ ÏŚ";
    String plText = "STANOWIĄCEJ CZĘŚĆ RODZINY JĘZYKÓW";
    String skText = "TU ŽIADNA VEĽKÁ JAZYKOVÁ ČISTKA";

    String lowerCase = text.toLowerCase(new Locale("fr"));
    String plLowerCase = plText.toLowerCase(new Locale("pl"));
    String skLowerCase = skText.toLowerCase(new Locale("sk"));

    assertEquals(lowerCase, "aç êè oč ïś");
    assertEquals(plLowerCase, "stanowiącej część rodziny języków");
    assertEquals(skLowerCase, "tu žiadna veľká jazyková čistka");
  }

  @Test
  public void shouldUpperCaseAccentedWordsProperly() {
    String huText = "szóösszetételekben";
    String ptText = "população";
    String ltText = "sovietmečiu mūšos tyrelio pelkė buvo numatyta durpių";

    String huUpperCase = huText.toUpperCase(new Locale("hu"));
    String ptUpperCase = ptText.toUpperCase(new Locale("pt"));
    String ltUpperCase = ltText.toUpperCase(new Locale("lt"));

    assertEquals(huUpperCase, "SZÓÖSSZETÉTELEKBEN");
    assertEquals(ptUpperCase, "POPULAÇÃO");
    assertEquals(ltUpperCase,
        "SOVIETMEČIU MŪŠOS TYRELIO PELKĖ BUVO NUMATYTA DURPIŲ");
  }

  @Test
  public void shouldHandleTurkishCorrectly() {
    String text1 = "tunçtan yapılmış olan heykelin yüksekliği";
    String text2 = "YAPMIŞ OLDUĞU ÖZVERİLİ ÇALIŞMALARI";

    String upper = text1.toUpperCase(new Locale("tr"));
    String lower = text2.toLowerCase(new Locale("tr"));

    assertEquals(upper, "TUNÇTAN YAPILMIŞ OLAN HEYKELİN YÜKSEKLİĞİ");
    assertEquals(lower, "yapmış olduğu özverili çalışmaları");
  }

}
