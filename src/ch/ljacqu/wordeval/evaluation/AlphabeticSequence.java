package ch.ljacqu.wordeval.evaluation;

import java.util.List;

public class AlphabeticSequence extends Evaluator<Integer, String> {

  public enum SearchDirection {
    FORWARDS(-1), BACKWARDS(1);

    SearchDirection(int compareValue) {
      this.shallCompareValue = compareValue;
    }

    int shallCompareValue;
  }
  
  private SearchDirection searchDirection;
  
  public AlphabeticSequence(SearchDirection direction) {
    searchDirection = direction;
  }
  
  @Override
  public void processWord(String word) {
    int alphabeticalStreak = 1;
    int maxStreak = 0;
    
    String previousChar = String.valueOf(word.charAt(0)).toLowerCase();
    for (int i = 1; i < word.length(); ++i) {
      boolean addToStreak = false;
      if (i < word.length()) {
        String currentChar = String.valueOf(word.charAt(i)).toLowerCase();
        addToStreak = previousChar.compareTo(currentChar) == searchDirection.shallCompareValue;
        if (addToStreak) {
          ++alphabeticalStreak;
        }
        previousChar = currentChar;
      }
      if (!addToStreak) {
        if (alphabeticalStreak > maxStreak) {
          maxStreak = alphabeticalStreak;
        }
        alphabeticalStreak = 1;
      }
    }
    if (maxStreak > 2) {
      addEntry(maxStreak, word);
    }
  }
  
  @Override
  protected void outputEntry(Integer key, List<String> entry) {
    System.out.println(key + " (" + entry.size() + "): " + entry);
  }

}
