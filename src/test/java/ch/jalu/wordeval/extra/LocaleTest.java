package ch.jalu.wordeval.extra;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests the general behavior of different Java locales.
 */
class LocaleTest {

  @Test
  void shouldLowerCaseAccentedWordsProperly() {
    String text = "AÇ Êè OČ ÏŚ";
    String plText = "STANOWIĄCEJ CZĘŚĆ RODZINY JĘZYKÓW";
    String skText = "TU ŽIADNA VEĽKÁ JAZYKOVÁ ČISTKA";

    String lowerCase = text.toLowerCase(Locale.of("fr"));
    String plLowerCase = plText.toLowerCase(Locale.of("pl"));
    String skLowerCase = skText.toLowerCase(Locale.of("sk"));

    assertThat(lowerCase, equalTo("aç êè oč ïś"));
    assertThat(plLowerCase, equalTo("stanowiącej część rodziny języków"));
    assertThat(skLowerCase, equalTo("tu žiadna veľká jazyková čistka"));
  }

  @Test
  void shouldUpperCaseAccentedWordsProperly() {
    String huText = "szóösszetételekben";
    String ptText = "população";
    String ltText = "sovietmečiu mūšos tyrelio pelkė buvo numatyta durpių";

    String huUpperCase = huText.toUpperCase(Locale.of("hu"));
    String ptUpperCase = ptText.toUpperCase(Locale.of("pt"));
    String ltUpperCase = ltText.toUpperCase(Locale.of("lt"));

    assertThat(huUpperCase, equalTo("SZÓÖSSZETÉTELEKBEN"));
    assertThat(ptUpperCase, equalTo("POPULAÇÃO"));
    assertThat(ltUpperCase, equalTo("SOVIETMEČIU MŪŠOS TYRELIO PELKĖ BUVO NUMATYTA DURPIŲ"));
  }

  @Test
  void shouldHandleTurkishCorrectly() {
    String text1 = "tunçtan yapılmış olan heykelin yüksekliği";
    String text2 = "YAPMIŞ OLDUĞU ÖZVERİLİ ÇALIŞMALARI";

    String upper = text1.toUpperCase(Locale.of("tr"));
    String lower = text2.toLowerCase(Locale.of("tr"));

    assertThat(upper, equalTo("TUNÇTAN YAPILMIŞ OLAN HEYKELİN YÜKSEKLİĞİ"));
    assertThat(lower, equalTo("yapmış olduğu özverili çalışmaları"));
  }

}
