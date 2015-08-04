package ch.ljacqu.wordeval.language;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import ch.ljacqu.wordeval.evaluation.Evaluator;

public abstract class Dictionary {
	
	@SuppressWarnings("unused")
	private String languageCode;
	private String fileName;
	private DictionaryType type;
	private List<Evaluator> evaluators;
	
	public Dictionary(String fileName,
			String languageCode,
			DictionaryType type, 
			List<Evaluator> evaluators) {
		this.languageCode = languageCode;
		this.fileName = fileName;
		this.type = type;
		this.evaluators = evaluators;
	}
	
	public abstract String sanitizeWord(String crudeWord);
	
	public final void processDictionary() throws IOException {	
		if (type == DictionaryType.WORD_PER_LINE) {
			loadLineDictionary();
		}
	}
	
	
	private void loadLineDictionary() throws IOException {
		FileInputStream fis = new FileInputStream(fileName);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		
		try (BufferedReader br = new BufferedReader(isr)) {
		    for (String line; (line = br.readLine()) != null; ) {
		    	if (!line.trim().isEmpty()) {
			        processWord(sanitizeWord(line));	
		    	}
		    }
		}
	}
	
	private void processWord(String word) {
		for (Evaluator evaluator : evaluators) {
			evaluator.processWord(word);
		}
	}

}
