package evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import main.VowelService;

public class VowelCount implements Evaluation {
	
	public enum SearchType {
		VOWELS(true),
		CONSONANTS(false);
		
		SearchType(boolean isVowel) {
			this.isVowel = isVowel;
		}
		boolean isVowel;
	}
	
	private boolean isVowel;
	
	private List<Character> recognizedVowels = VowelService.getExtendedVowels();

	private Map<Integer, List<String>> vowels = new HashMap<>();
	
	public VowelCount(SearchType type) {
		isVowel = type.isVowel;
	}

	
	@Override
	public void processWord(String word) {
		int count = 0;
		for (int i = 0; i <= word.length(); ++i) {
			if (i == word.length() || recognizedVowels.contains(word.charAt(i)) != isVowel) {
				if (count > 1) {
					addEntry(count, word);
				}
				count = 0;
			} else {
				++count;
			}
		}
	}
	
	private void addEntry(Integer key, String value) {
		if (vowels.get(key) == null) {
			vowels.put(key, new ArrayList<>());
		}
		vowels.get(key).add(value);
	}
	
	@Override
	public void outputAggregatedResult() {
		for (Entry<Integer, List<String>> entry : vowels.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue().size());
			if (entry.getKey() >= 4) {
				System.out.println(entry.getValue());
			}
		}
	}
	
}
