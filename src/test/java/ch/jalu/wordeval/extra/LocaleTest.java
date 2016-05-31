package ch.jalu.wordeval.extra;

import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Tests the general behavior of different Java locales.
 */
public class LocaleTest {

  @Test
  public void shouldLowerCaseAccentedWordsProperly() {
    String text = "AÇ Êè OČ ÏŚ";
    String plText = "STANOWIĄCEJ CZĘŚĆ RODZINY JĘZYKÓW";
    String skText = "TU ŽIADNA VEĽKÁ JAZYKOVÁ ČISTKA";

    String lowerCase = text.toLowerCase(new Locale("fr"));
    String plLowerCase = plText.toLowerCase(new Locale("pl"));
    String skLowerCase = skText.toLowerCase(new Locale("sk"));

    assertThat(lowerCase, equalTo("aç êè oč ïś"));
    assertThat(plLowerCase, equalTo("stanowiącej część rodziny języków"));
    assertThat(skLowerCase, equalTo("tu žiadna veľká jazyková čistka"));
  }

  @Test
  public void shouldUpperCaseAccentedWordsProperly() {
    String huText = "szóösszetételekben";
    String ptText = "população";
    String ltText = "sovietmečiu mūšos tyrelio pelkė buvo numatyta durpių";

    String huUpperCase = huText.toUpperCase(new Locale("hu"));
    String ptUpperCase = ptText.toUpperCase(new Locale("pt"));
    String ltUpperCase = ltText.toUpperCase(new Locale("lt"));

    assertThat(huUpperCase, equalTo("SZÓÖSSZETÉTELEKBEN"));
    assertThat(ptUpperCase, equalTo("POPULAÇÃO"));
    assertThat(ltUpperCase, equalTo("SOVIETMEČIU MŪŠOS TYRELIO PELKĖ BUVO NUMATYTA DURPIŲ"));
  }

  @Test
  public void shouldHandleTurkishCorrectly() {
    String text1 = "tunçtan yapılmış olan heykelin yüksekliği";
    String text2 = "YAPMIŞ OLDUĞU ÖZVERİLİ ÇALIŞMALARI";

    String upper = text1.toUpperCase(new Locale("tr"));
    String lower = text2.toLowerCase(new Locale("tr"));

    assertThat(upper, equalTo("TUNÇTAN YAPILMIŞ OLAN HEYKELİN YÜKSEKLİĞİ"));
    assertThat(lower, equalTo("yapmış olduğu özverili çalışmaları"));
  }

}
