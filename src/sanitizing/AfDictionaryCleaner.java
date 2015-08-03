package sanitizing;

public class AfDictionaryCleaner implements Sanitizer {

	@Override
	public String sanitizeWord(String word) {
		int dashPosition = word.indexOf('/');
		if (dashPosition != -1) {
			return word.substring(0, dashPosition);
		}
		return word;
	}
	
}
