package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import sanitizing.Sanitizer;
import evaluation.Evaluation;


public class DictionaryLoader {

	private Evaluation evaluator;
	private Sanitizer sanitizer;
	
	public DictionaryLoader(String filename, Evaluation evaluator, Sanitizer sanitizer)
			throws IOException {
		this.evaluator = evaluator;
		this.sanitizer = sanitizer;
		loadFile(filename);
	}
	
	private void loadFile(String filename) throws IOException {
		File file = new File(filename);
		FileInputStream fis = new FileInputStream(filename);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		
		try (BufferedReader br = new BufferedReader(isr)) {
		    for (String line; (line = br.readLine()) != null; ) {
		    	if (!line.trim().isEmpty()) {
			        evaluator.processWord( sanitizer.sanitizeWord(line) );	
		    	}
		    }
		}
	}

}
