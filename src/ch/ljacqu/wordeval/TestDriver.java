package ch.ljacqu.wordeval;

import static ch.ljacqu.wordeval.language.LetterType.CONSONANTS;
import static ch.ljacqu.wordeval.language.LetterType.VOWELS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.ljacqu.wordeval.dictionary.Dictionary;
import ch.ljacqu.wordeval.evaluation.Anagrams;
import ch.ljacqu.wordeval.evaluation.Evaluator;
import ch.ljacqu.wordeval.evaluation.MonotoneVowel;
import ch.ljacqu.wordeval.evaluation.TestEvaluator;
import ch.ljacqu.wordeval.language.Alphabet;
import ch.ljacqu.wordeval.language.Language;

class TestDriver {
  
  static {
    AppData.init();
  }
  
  public static void main(String[] args) throws IOException {
    Language lang = new Language("hu", Alphabet.LATIN);
    List<Evaluator<?>> evaluators = new ArrayList<>();
    evaluators.add(new Anagrams());
    evaluators.add(new TestEvaluator(CONSONANTS));
    evaluators.add(new MonotoneVowel(VOWELS, lang));
    evaluators.add(new MonotoneVowel(CONSONANTS, lang));
    
    Dictionary d = Dictionary.getDictionary("hu");
    
    d.process(evaluators);
  }

}
