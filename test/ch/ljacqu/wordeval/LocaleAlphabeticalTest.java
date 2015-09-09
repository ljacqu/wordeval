package ch.ljacqu.wordeval;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import java.text.Collator;
import java.util.Locale;
import org.junit.Test;

public class LocaleAlphabeticalTest {

  @Test
  public void shouldCompareWithSwedishRules() {
    Collator collator = Collator.getInstance(new Locale("sv"));

    shouldCompareTo(collator, 1, "ää", "az");
    shouldCompareTo(collator, 1, "åa", "az");
    shouldCompareTo(collator, 1, "öö", "zz");
    shouldCompareTo(collator, 1, "f", "ê");
    // Note, compare(ê, e) yields 1 even with Swedish locale. This means that
    // the locale of the language can very well be used to determine whether
    // something is alphabetical or not, but we should only do this with
    // native-only characters, i.e. use the NO_ACCENT word form and retain the
    // additional letters with the according setting in the Language class.
    shouldCompareTo(collator, 1, "ê", "e");
    shouldCompareTo(collator, 1, "ê", "é");
  }

  @Test
  public void shouldCompareWithGermanRules() {
    Collator collator = Collator.getInstance(new Locale("de"));

    shouldCompareTo(collator, -1, "ää", "az");
    shouldCompareTo(collator, -1, "öö", "zz");
    shouldCompareTo(collator, -1, "ça", "cb");
    shouldCompareTo(collator, -1, "aßa", "assb");
  }

  @Test
  public void shouldCompareWithHungarianRules() {
    Collator collator = Collator.getInstance(new Locale("hu"));

    // D, Dz, Dzs; G, Gy; L, Ly; N, Ny; S, Sz; T, Ty; Z, Zs
    shouldCompareTo(collator, 1, "Gya", "Gza");
    shouldCompareTo(collator, 1, "Csa", "Czz");
    shouldCompareTo(collator, 1, "Dzs", "Dzz");
    shouldCompareTo(collator, 1, "Zsa", "Zzz");
    shouldCompareTo(collator, 1, "Lya", "Lzz");

    shouldCompareTo(collator, -1, "áa", "ab");
    shouldCompareTo(collator, -1, "óa", "oab");
    shouldCompareTo(collator, -1, "úa", "ub");
    shouldCompareTo(collator, 1, "üa", "uz");
    shouldCompareTo(collator, 1, "űa", "uz");
    // ű and ü should be treated the same in ordering, but this doesn't seem to
    // be the case here...
    // shouldCompareTo(collator, -1, "űa", "uz");
    // shouldCompareTo(collator, -1, "őa", "öb");
    shouldCompareTo(collator, 1, "öa", "oz");
  }

  @Test
  public void shouldCompareWithModernDutchRules() {
    Collator collator = Collator.getInstance(new Locale("nl"));

    // The collator correctly uses the more modern sorting where ij is
    // treated "normally." Before, IJ was between X and Z.
    shouldCompareTo(collator, -1, "ijsjes", "ik");
    shouldCompareTo(collator, -1, "rij", "rillen");

    // The digraph "ĳ" seems completely unrecognized and is even sorted behind
    // z. It's only a legacy character anyway and we shouldn't encounter it.
    // shouldCompareTo(collator, -1, "rĳ", "rz");
  }

  public static void shouldCompareTo(Collator collator, int expected,
      String... words) {
    assertThat(collator.compare(words[0], words[1]), equalTo(expected));
  }

  // From https://docs.oracle.com/javase/tutorial/i18n/text/locale.html
  public static void sortStrings(Collator collator, String[] words) {
    String tmp;
    for (int i = 0; i < words.length; i++) {
      for (int j = i + 1; j < words.length; j++) {
        if (collator.compare(words[i], words[j]) > 0) {
          tmp = words[i];
          words[i] = words[j];
          words[j] = tmp;
        }
      }
    }
  }

}
