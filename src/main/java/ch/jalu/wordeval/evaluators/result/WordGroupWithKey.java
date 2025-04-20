package ch.jalu.wordeval.evaluators.result;

import ch.jalu.wordeval.dictionary.Word;

import java.util.Set;

public record WordGroupWithKey(Set<Word> words, String key) {

}
