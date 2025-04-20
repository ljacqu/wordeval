_wordeval_
==========
[![Build Status](https://github.com/ljacqu/wordeval/workflows/build/badge.svg)](https://github.com/ljacqu/wordeval/actions?query=workflow%3A%22build%22)
[![Coverage Status](https://coveralls.io/repos/ljacqu/wordeval/badge.svg?branch=master&service=github)](https://coveralls.io/github/ljacqu/wordeval?branch=master)
[![Code Climate](https://codeclimate.com/github/ljacqu/wordeval/badges/gpa.svg)](https://codeclimate.com/github/ljacqu/wordeval)

Java application that processes dictionaries of various languages to find "interesting" words based on
various criteria. The results are exported as JSON.


Requirements
------------
- Java 21
- Requires you install the [Lombok plugin](https://projectlombok.org/download.html) for your IDE or you won't see the
  accessors and builders it generates
- Maven


Technical layout
----------------
- `Language` contains some basic definitions about a language, such as which characters it considers as vowels.
- A `Dictionary` has some basic rules about how to read a dictionary file and which language it belongs to. It can
  create a `Sanitizer`, which processes the dictionary's lines and extracts the words from it for further use.
- Each `Evaluator` processes words based on some criteria to find the most (for its criteria) "interesting" ones.
  It then allows to export the most relevant ones.
