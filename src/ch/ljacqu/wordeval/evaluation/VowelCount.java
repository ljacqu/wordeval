package ch.ljacqu.wordeval.evaluation;

import java.util.List;

import ch.ljacqu.wordeval.VowelService;

public class VowelCount extends Evaluator<Integer, String> {
	
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
	
	@Override
	public void outputEntry(Integer key, List<String> entry) {
	  System.out.println(key + ": " + entry.size());
	  if (key > 4) {
	    System.out.println(entry);
		}
	}
	
}
