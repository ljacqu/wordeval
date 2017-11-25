package ch.jalu.wordeval.propertytransformers;

import ch.jalu.wordeval.dictionary.Word;

import java.util.List;

/**
 * Created by ljacqu on 18.03.17.
 */
public interface PropertyFinder<T> {

    List<T> findProperties(Word word);

}
