package ch.jalu.wordeval.evaluators.result;

import ch.jalu.wordeval.dictionary.Word;

public record WordWithKeyAndScore(Word word, String key, int score) {

}
