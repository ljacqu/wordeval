_wordeval_
==========
Java application that processes dictionaries of various languages to find "interesting" words according to 
different criteria. The results are exported as JSON so they can be displayed by another module (planned).


Status
------
In development — a lot of refactoring and major changes are still taking place.


Technical layout
----------------
An `Evaluator` processes words and decides (based on its criteria) whether it keeps the words in its results. 
A `Dictionary` object takes a list of _evaluators_ as argument. As it loads a new word, it passes it to its 
list of _evaluators_ (allowing them to decide how "interesting" that word is individually). Once the 
_dictionary_ is done, the result of each evaluator is converted to an `ExportObject`; all together are then 
exported as JSON.

Certain _evaluators_ yield a lot of results; we only keep the most interesting results in the export object 
and all others are aggregated, i.e. we only store the number of words found for the remaining entries.

The unit tests currently cover 80% of the source code and can serve as a good entry point to understand the
purpose of a class.


Evaluator example
-----------------
Consider the _Isograms_ evaluator that keeps isograms (words whose letters only appear once). It has the 
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

