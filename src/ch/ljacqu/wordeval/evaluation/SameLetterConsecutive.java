package ch.ljacqu.wordeval.evaluation;


public class SameLetterConsecutive extends Evaluator<String, String> {
	
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
	
	private String repeatChar(char chr, int times) {
		String str = "";
		for (int i = 0; i < times; ++i) {
			str += chr;
		}
		return str;
	}

}
