Petit Poucet: a general purpose explainability library
======================================================

The theory behind Petit Poucet, along with a short description of the library,
have been published in this research paper:

- S. Hallé, H. Tremblay. (2021). Foundations of Fine-Grained Explainability. In
  *Proc. 33rd Intl. Conf. on Computer Aided Verification (CAV 2021)*. Springer:
  Lecture Notes in Computer Science 12760, 500-523. DOI:
  [10.1007/978-3-030-81688-9_24](https://doi.org/10.1007/978-3-030-81688-9_24)

Building Petit Poucet
---------------------

First make sure you have the following installed:

- The Java Development Kit (JDK) to compile. Petit Poucet requires version 8
  of the JDK (and probably works with later versions).
- [Ant](http://ant.apache.org) to automate the compilation and build process

Download the sources for Petit Poucet from
[GitHub](http://github.com/liflab/petitpoucet) or clone the repository
using Git:

    git clone git@github.com:liflab/petitpoucet.git

### Project structure

The 

### Compiling

Open a command prompt to the `Source` folder of the project. First, download
the dependencies by typing:

    ant download-deps

Then, compile the sources by simply typing:

    ant

This will produce several files called `petitpoucet-XXX.jar` in the folder.
In addition, the script generates in the `docs/doc` folder the Javadoc
documentation for using Petit Poucet, along with commented code examples.

### Testing

Petit Poucet can test itself by running:

    ant test

Unit tests are run with [jUnit](http://junit.org); a detailed report of
these tests in HTML format is available in the folder `tests/junit`, which
is automatically created. Code coverage is also computed with
[JaCoCo](http://www.eclemma.org/jacoco/); a detailed report is available
in the folder `tests/coverage`.

Who uses Petit Poucet?
----------------------

Petit Poucet is used as a library in a few projects, including the following:

- [TeXtidote](https://github.com/sylvainhalle/TeXtidote), a linter and grammar
  checker for LaTeX files. TeXtidote checks the grammar by first "cleaning" a
  LaTeX file of all its markup, and sending the clear text to a grammar
  checking library (namely [LanguageTool](https://languagetool.org)). The
  cleaning operations are performed by successively applying Petit Poucet's
  string manipulation functions on the original file. The explainability
  mechanism of Petit Poucet then makes it possible to translate errors found
  in the clean file to their corresponding location in the original source
  file.

- [Synthia](https://github.com/liflab/synthia), a data structure generator.
  In this library, individual generators (called "pickers") can be composed to
  produce intricate data structures. Each picker implements the `Queryable`
  interface of Petit Poucet, and thus parts of an output structure can be
  retraced all the way up to the individual values that contributed to its
  creation.

- [The Programmatic Spreadsheet](https://github.com/liflab/programmatic-spreadsheet),
  a library that allows users to create and manipulate spreadsheet-like data
  structures (still under construction).

About the author
----------------

Petit Poucet was written by [Sylvain Hallé](https://leduotang.ca/sylvain), Full
Professor in the Department of Computer Science and Mathematics at
[Université du Québec à Chicoutimi](http://www.uqac.ca), Canada.

<!-- :wrap=soft: -->