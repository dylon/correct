Correct
=======

Introduction
------------

Correct is a Clojure implementation of a spelling corrector by Peter Norvig.  To
learn more about how it works and the reasoning behind it, please visit [How to
Write a Spelling Corrector][1].

Usage
-----

	$ clj # Initialize the Clojure REPL
	user=> (load "correct") ; Load the correction routines
	user=> (correct "tets") ; -> "test"
	user=> (correct "correct") ; -> "correct"
	user=> (correct "novelword") ; -> "novelword"

Sources
-------

1. [How to Write a Spelling Corrector][1]
2. [Clojure - Functional Programming for the JVM][2]
2. [Understanding Data Processing with Map/Reduce][3]

[1]: http://norvig.com/spell-correct.html "How to Write a Spelling Corrector"
[2]: http://java.ociweb.com/mark/clojure/article.html "Clojure - Functional Programming for the JVM"
[3]: http://www.oracle.com/technetwork/community/bookstore/sample-clojure-485732.pdf "Understanding Data Processing with Map/Reduce"

