package ch.jalu.wordeval.extra;

import org.junit.jupiter.api.Test;

import java.text.Collator;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests that show the general behavior of the collator coupled with a specific
 * locale.
 */
class LocaleAlphabeticalTest {

  /**
   * Note that a collator may even return a specific sort order for letters with
   * accents the language doesn't have. E.g. in Swedish, the order is that e
   * comes before ê, and ê comes before è.
   * <p>
   * It's fine to use the collators to work with words in a language according
   * to its rules, but it may be most sensible to use the NO_ACCENT word form
   * such that only the real, distinct letters are retained.
   */
  @Test
  void generalComparison() {
    Collator collator = Collator.getInstance(Locale.of("sv"));

    shouldCompareTo(collator, 1, "ê", "e");
    shouldCompareTo(collator, 1, "ê", "è");
    shouldCompareTo(collator, 1, "f", "ê");
  }

  @Test
  void csComparisonTest() {
    // Ch is a separate letter: H < Ch < I
    // CČ, RŘ, SŠ, ZŽ are seen as distinct letters following each other
    // All other accents (e.g. NŇ, UÚŮ) have same sorting order
    Collator collator = Collator.getInstance(Locale.of("cs"));
    String[] words = { "azerty", "ábc", "žal", "zzz", "děc", "ded", "déa",
        "uchor", "uhor", "uko", "eša", "ese", "cut", "čat", "ory", "ořa",
        "půe", "puf", "púb", "nob", "ňóa", "učar", "ucor", "vuse", "vuša" };

    sortStrings(collator, words);

    String[] results = { "ábc", "azerty", "cut", "čat", "déa", "děc", "ded",
        "ese", "eša", "ňóa", "nob", "ory", "ořa", "púb", "půe", "puf", "ucor",
        "učar", "uhor", "uchor", "uko", "vuse", "vuša", "zzz", "žal" };
    assertThat(words, equalTo(results));
  }

  @Test
  void deComparisonTest() {
    Collator collator = Collator.getInstance(Locale.of("de"));

    shouldCompareTo(collator, -1, "ää", "az");
    shouldCompareTo(collator, -1, "öö", "zz");
    shouldCompareTo(collator, -1, "ça", "cb");
    shouldCompareTo(collator, -1, "aßa", "assb");
  }

  @Test
  void huComparisonTest() {
    Collator collator = Collator.getInstance(Locale.of("hu"));

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
  void nlComparisonTest() {
    Collator collator = Collator.getInstance(Locale.of("nl"));

    // The collator correctly uses the more modern sorting where ij is
    // treated "normally." Before, IJ was between X and Z.
    shouldCompareTo(collator, -1, "ijsjes", "ik");
    shouldCompareTo(collator, -1, "rij", "rillen");

    // The digraph "ĳ" seems completely unrecognized and is even sorted behind
    // z. It's only a legacy character anyway and we shouldn't encounter it.
    // shouldCompareTo(collator, -1, "rĳ", "rz");
  }

  @Test
  void svComparisonTest() {
    Collator collator = Collator.getInstance(Locale.of("sv"));

    shouldCompareTo(collator, 1, "ää", "az");
    shouldCompareTo(collator, 1, "åa", "az");
    shouldCompareTo(collator, 1, "öö", "zz");
  }

  @Test
  void trComparisonTest() {
    Collator collator = Collator.getInstance(Locale.of("tr"));

    // Ensure that {g, ı, o, s} come before {ğ, i, ö, ş}, respectively
    shouldCompareTo(collator, -1, "gzz", "ğaa");
    shouldCompareTo(collator, -1, "ızz", "iaa");
    shouldCompareTo(collator, -1, "ozz", "öaa");
    shouldCompareTo(collator, -1, "szz", "şaa");
    shouldCompareTo(collator, -1, "şzz", "taa");
  }

  private static void shouldCompareTo(Collator collator, int expected,
      String word1, String word2) {
    assertThat(collator.compare(word1, word2), equalTo(expected));
  }

  // From https://docs.oracle.com/javase/tutorial/i18n/text/locale.html
  private static void sortStrings(Collator collator, String[] words) {
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
