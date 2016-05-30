_wordeval_
==========
<a href="https://travis-ci.org/ljacqu/wordeval">![Build Status]
(https://travis-ci.org/ljacqu/wordeval.svg?branch=master)</a> <a href="https://coveralls.io/github/ljacqu/wordeval?branch=master">![Coverage Status]
(https://coveralls.io/repos/ljacqu/wordeval/badge.svg?branch=master&service=github)</a> [![Dependency Status]
(https://www.versioneye.com/user/projects/572e346fa0ca350034be705b/badge.svg?style=flat)](https://www.versioneye.com/user/projects/572e346fa0ca350034be705b) [![Codacy Badge]
(https://api.codacy.com/project/badge/Grade/8b630febc46242f594970a4aba8cf0d5)](https://www.codacy.com/app/ljacqu/wordeval?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ljacqu/wordeval&amp;utm_campaign=Badge_Grade)
<br />
Java application that processes dictionaries of various languages to find "interesting" words based on
various criteria. The results are exported as JSON so they can be displayed by another module (planned).


Requirements
------------
- Requires Java 8
- Requires you install the [Lombok plugin](https://projectlombok.org/download.html) for your IDE or you won't see the
  accessors and builders it generates
- Project is set up with Maven. If you don't have Maven, you need to download the dependencies defined in the pom.xml 
  file manually.


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
