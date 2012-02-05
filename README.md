Correct
=======

Introduction
------------

Correct is a Clojure implementation of a spelling corrector by Peter Norvig.  To
learn more about how the spelling corrector works and the reasoning behind it,
please visit [How to Write a Spelling Corrector][1]; to learn more about how
map/reduce works and how it has been implemented here, please visit
[Understanding Data Processing with Map/Reduce][2]; to learn more about Clojure
in general, please visit [Clojure - Functional Programming for the JVM][3].

Usage
-----

	$ clj
	user=> (use '[spelling.correction :only [correct]])
	user=> (correct "tets")      ; -> "test"
	user=> (correct "correct")   ; -> "correct"
	user=> (correct "novelword") ; -> "novelword"

Sources
-------

1. [How to Write a Spelling Corrector][1]
2. [Understanding Data Processing with Map/Reduce][2]
3. [Clojure - Functional Programming for the JVM][3]

[1]: http://norvig.com/spell-correct.html "How to Write a Spelling Corrector"
[2]: http://www.oracle.com/technetwork/community/bookstore/sample-clojure-485732.pdf "Understanding Data Processing with Map/Reduce"
[3]: http://java.ociweb.com/mark/clojure/article.html "Clojure - Functional Programming for the JVM"

