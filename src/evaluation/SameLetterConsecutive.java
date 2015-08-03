package evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SameLetterConsecutive implements Evaluation {
	
	private Map<String, List<String>> letterGroups = new HashMap<>();
	
	@Override
	public void processWord(String word) {
		String sWord = word.trim().toLowerCase();
		int counter = 0;
		char lastChar = '\0';
		for (int i = 0; i <= sWord.length(); ++i) {
			if (i < sWord.length() && sWord.charAt(i) == lastChar) {
				++counter;
			} else {
				if (counter > 1) {
					addEntry(repeatChar(lastChar, counter), word);
				}
				lastChar = i < sWord.length() ? sWord.charAt(i) : '\0';
				counter = 1;
			}
		}
	}
	
	private void addEntry(String key, String value) {
		if (letterGroups.get(key) == null) {
			letterGroups.put(key, new ArrayList<>());
		}
		letterGroups.get(key).add(value);
	}
	
	private String repeatChar(char chr, int times) {
		String str = "";
		for (int i = 0; i < times; ++i) {
			str += chr;
		}
		return str;
	}
	
	@Override
	public void outputAggregatedResult() {
		for (Entry<String, List<String>> entry : letterGroups.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue().size());
		}
	}

}
