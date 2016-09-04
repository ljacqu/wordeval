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
- BackwardsPairs: Word pairs that are equal to each other when reversed, e.g. parts and strap
- ConsecutiveLetterPairs: Words with multiple letter pairs following each other, e.g. b**ookkee**per, ta**ttoo**ist
- ConsecutiveVowelCount: Words with vowel or consonant clusters, e.g. aq**ueou**s, obseq**uiou**s
- FullPalindromes: Words that are entirely palindromes, e.g. Malayalam, redder
- Isograms: Words with no repeating letter, e.g. considerably, demographics
- LongWords: Longest words... e.g., electroencephalographic, institutionalization
- MonotoneVowel: Words with only one different vowel/consonant, e.g. d**e**f**e**ns**e**l**e**ssn**e**ss, 
  a**c**a**c**ia 
- Palindromes: Unlike FullPalindromes, also words with palindrome sequences, e.g. **sensuousnes**s, pr**ecipice**
- SameLetterConsecutive: Repetitions of the same letter, e.g. Russian голош**еее**, German Ro**lll**aden
