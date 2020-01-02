_wordeval_
==========
[![Build Status](https://travis-ci.org/ljacqu/wordeval.svg?branch=master)](https://travis-ci.org/ljacqu/wordeval)
[![Coverage Status](https://coveralls.io/repos/ljacqu/wordeval/badge.svg?branch=master&service=github)](https://coveralls.io/github/ljacqu/wordeval?branch=master)
[![Code Climate](https://codeclimate.com/github/ljacqu/wordeval/badges/gpa.svg)](https://codeclimate.com/github/ljacqu/wordeval)

Java application that processes dictionaries of various languages to find "interesting" words based on
various criteria. The results are exported as JSON so they can be displayed by another module (planned).


Requirements
------------
- Java 11
- Requires you install the [Lombok plugin](https://projectlombok.org/download.html) for your IDE or you won't see the
  accessors and builders it generates
- Maven


General setup
-------------
- Indentation with two spaces
- 120 characters as max line width
- JavaDoc on all _public_ members


Technical layout
----------------
An `Evaluator` processes words and decides (based on its criteria) whether it keeps the words in its results. 
A `Dictionary` object takes a list of _evaluators_ as argument. As it loads the words from its file, it passes 
it to the list of _evaluators_ it received, allowing them to decide how "interesting" that word is individually. 
Once the _dictionary_ has finished, the result of each evaluator is converted to an `ExportObject`; all together 
are then exported as JSON.

Certain _evaluators_ yield a lot of results; we only keep the most interesting results in the export object 
and all others are aggregated, i.e. we only store the number of words found for the remaining entries.

The current tests yield a coverage of 80% over the project's code base and can serve as a good entry point to 
understand the purpose of a class.
