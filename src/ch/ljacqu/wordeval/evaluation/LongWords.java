package ch.ljacqu.wordeval.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Map.Entry;

public class LongWords implements Evaluator {
	
	/** Ignore any words whose length is less than the minimum length. */
	public static final int DEFAULT_MIN_LENGTH = 6;
	
	private int minLength = DEFAULT_MIN_LENGTH;
	
	private Map<Integer, List<String>> longWords = new HashMap<>();
	
	public LongWords() {
	}
	
	public LongWords(int minLength) {
		this.minLength = minLength;
	}
	
	@Override
	public void processWord(String word) {
		if (word.length() < minLength) {
			return;
		}
		int length = word.length();
		if (longWords.get(length) == null) {
			longWords.put(length, new ArrayList<String>());
		}
		longWords.get(length).add(word);
		
	}
	
	@Override
	public void outputAggregatedResult() {
		for (Entry<Integer, List<String>> entry : longWords.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue().size());
		}
	}
	

}
