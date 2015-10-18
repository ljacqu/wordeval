Evaluators
==========

Evaluator example
-----------------
Consider the _Isograms_ evaluator that keeps isograms (words whose letters only occur once). It has the 
following result after a dictionary has been processed. The number is the length of the word; the longer the 
word is, the cooler it is!

```javascript
results = {
  14: ["halfduimspyker", "wegkantdryfhou"],
  13: ["belastingdruk", "greinhoutbalk", "verontskuldig"],
  12: ["behoudsparty", "christenvolk", "produksielyn"],
  11: ["sportwinkel", "toeskrywing"]
}
```

We define that we only want to keep the top two lengths, so the export object for this evaluator will be the 
following:

```json
{
 "topEntries": {
   "14": ["halfduimspyker", "wegkantdryfhou"],
   "13": ["belastingdruk", "greinhoutbalk", "verontskuldig"]
 },
 "aggregatedEntries": {
   "12": 3,
   "11": 2
 },
 "identifier": "Isograms"
}
```

The _topEntries_ key contains the list of words we decided to keep (the coolest). For the less cool lengths, 
we just store the number of words we found for them in _aggregatedEntries_. An _identifier_ defines what 
evaluator the result is for (typically the class name), since the end result will be a JSON file of multiple 
such export objects.

List of Evaluators
------------------
- AlphabeticalOrder: Words whose letters are in alphabetical order, e.g. billowy, access
- AlphabeticalSequence: Words with sequences in direct alphabetical order, e.g. ove**rstu**ffed, lau**ghi**ng
- Anagrams: Groups words that are anagrams, e.g. abel, able and bale
- ConsecutiveLetterPairs: Words with multiple letter pairs following each other, e.g. b**ookkee**per, ta**ttoo**ist
- ConsecutiveVowelCount: Words with vowel or consonant clusters, e.g. aq**ueou**s, obseq**uiou**s
- FullPalindromes: Words that are entirely palindromes, e.g. Malayalam, redder
- Isograms: Words with no repeating letter, e.g. considerably, demographics
- LongWords: Longest words... e.g., electroencephalographic, institutionalization
- MonotoneVowel: Words with only one different vowel/consonant, e.g. d**e**f**e**ns**e**l**e**ssn**e**ss, 
  a**c**a**c**ia 
- Palindromes: Unlike FullPalindromes, also words with palindrome sequences, e.g. **sensuousnes**s, pr**ecipice**
- SameLetterConsecutive: Repetitions of the same letter, e.g. Russian голош**еее**, German Ro**lll**aden

PostEvaluators
--------------
Unlike regular evaluators, a class can annotate a method with @PostEvaluator to do a computation over another
evaluator's results (as to avoid repetitive tasks). For example, `Fullpalindromes` simply uses the results by
`Palindromes` since its results are a subset of the latter.

@PostEvaluator methods must have exactly one parameter of the desired `Evaluator` subtype, e.g.
``` java
@PostEvaluator
public void process(Palindromes evaluator) {
  // Do something with the Palindromes evaluator
}
```

If the "base evaluator" cannot be found alone by type, an additional method with @BaseMatcher can be defined. It must
have the same parameter as the @PostEvaluator method and returns a boolean to indicate whether or not the given
evaluator can be used:
``` java
@BaseMatcher
public boolean isMatch(Palindromes evaluator) {
  return true;
}
```

If any PostEvaluator cannot be matched with a base evaluator, an exception will be thrown.

The methods may not be private and may not take additional arguments.