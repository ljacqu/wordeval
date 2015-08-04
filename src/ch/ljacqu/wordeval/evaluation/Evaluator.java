package ch.ljacqu.wordeval.evaluation;

public interface Evaluator {
	
	void processWord(String word);
	
	void outputAggregatedResult();

}
