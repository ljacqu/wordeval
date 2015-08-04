package ch.ljacqu.wordeval.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ConsecutiveLetterPairs implements Evaluator {
	
	private Map<Integer, List<String>> foundWords = new HashMap<>();
	
	@Override
	public void processWord(String word) {
		String sWord = word.trim().toLowerCase();
		int letterCounter = 0;
		int pairCounter = 0;
		int pairCountMax = 0;
		char lastChar = '\0';
		for (int i = 0; i <= sWord.length(); ++i) {
			if (i < sWord.length() && sWord.charAt(i) == lastChar) {
				++letterCounter;
			} else {
				if (letterCounter > 1) {
					++pairCounter;
					if (pairCounter > pairCountMax) {
						pairCountMax = pairCounter;
					}
				} else {
					pairCounter = 0;
				}
				lastChar = i < sWord.length() ? sWord.charAt(i) : '\0';
				letterCounter = 1;
			}
		}
		if (pairCountMax > 1) {
			addEntry(pairCountMax, word);
		}
	}
	
	private void addEntry(Integer key, String value) {
		if (foundWords.get(key) == null) {
			foundWords.put(key, new ArrayList<>());
		}
		foundWords.get(key).add(value);
	}
	
	@Override
	public void outputAggregatedResult() {
		for (Entry<Integer, List<String>> entry : foundWords.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue().size());
		}
	}

}
