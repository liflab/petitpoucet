Petit Poucet: a general purpose explainability library
======================================================
[![Java CI](https://github.com/liflab/petitpoucet/actions/workflows/ant.yml/badge.svg)](https://github.com/liflab/petitpoucet/actions/workflows/ant.yml)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=liflab_petitpoucet&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=liflab_petitpoucet)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=liflab_petitpoucet&metric=coverage)](https://sonarcloud.io/summary/new_code?id=liflab_petitpoucet)

*Explainability* is the process of linking part of the inputs given to a
calculation to its output, in such a way that the selected inputs somehow
"cause" the result. *Petit Poucet* is a fully-functioning Java library allowing
users to create their own complex functions by composing built-in primitives,
and that can automatically produce an explanation for any result computed by
these functions on a given input.

The theory behind Petit Poucet, along with a short description of the library,
have been published in this research paper:

- S. Hallé, H. Tremblay. (2021). Foundations of Fine-Grained Explainability. In
  *Proc. 33rd Intl. Conf. on Computer Aided Verification (CAV 2021)*. Springer:
  Lecture Notes in Computer Science 12760, 500-523. DOI:
  [10.1007/978-3-030-81688-9_24](https://doi.org/10.1007/978-3-030-81688-9_24)

An example
----------

(This is just one example. Go to the [API
documentation](https://liflab.github.io/petitpoucet/javadoc/) to find many other
commented examples, or look directly at their [source
code](https://github.com/liflab/petitpoucet/tree/master/Source/Examples/src/examples)
in the project's repository.)

Computations are represented in Petit Poucet by the composition of *functions* transforming an input into an output, in such a way that parts of the output can be associated to parts of the input. Each function represents an elementary transformation, and complex calculations are achieved by composing or "piping" these functions together to form function *circuits*.

As an example, consider the following text file:

	the,2,penny
	fool,7,lane
	on,18,come
	the,2,together
	hill,-80,i
	strawberry,7,am
	fields,1,the
	forever,10,walrus

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

Petit Poucet is structured with a small *Core* library containing only the
essential classes and interfaces that are required in any project. In the
repository, this corresponds to the `Source/Core` folder, which compiles into
the `petitpoucet-core.jar` file.

Extensions specific to some use cases are located in the remaining folders
inside `Source`. For instance, the `Functions` folder compiles into a library
named `petitpoucet-functions.jar`, and, as its name implies, defines elementary
functions for arithmetic, vector and string manipulation.

### Compiling

Open a command prompt to the `Source` folder of the project. Then, compile the
sources by simply typing:

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

- [LabPal](https://liflab.github.io/labpal), an environment for creating,
  running and processing the results of experiments run on a computer. LabPal
  keeps track of each datapoint generated by an experiment. These datapoints are
  given unique IDs and can be queried through a web interface. In addition,
  LabPal can display the complete chain of transformations leading from raw data
  to any individual value computed by a lab. Table cells and macros exported to
  LaTeX have PDF hyperlinks containing the unique ID of each datapoint, making
  it possible to retrace every individual value included in a PDF back to the
  lab's experiment where it was computed. All these features are implemented
  using PetitPoucet's objects under the hood.

- [lif-units](https://github.com/liflab/lif-units), a library that manipulates
  and converts quantities expressed in dimensional units with uncertainty.
  Operations on these units are descendants of Petit Poucet's arithmetic
  functions, and hence inherit their explainability features.

- [The Programmatic
  Spreadsheet](https://github.com/liflab/programmatic-spreadsheet), a library
  that allows users to create and manipulate spreadsheet-like data structures.

Why is it called Petit Poucet?
------------------------------

"Petit Poucet" translates in English to "Hop-o'-My-Thumb". It is the title of a
a fairy tale by French writer Charles Perrault where the main character uses
stones to mark a trail that enables him to successfully lead his lost brothers
back home.

About the author
----------------

Petit Poucet was written by [Sylvain Hallé](https://leduotang.ca/sylvain), Full
Professor in the Department of Computer Science and Mathematics at
[Université du Québec à Chicoutimi](http://www.uqac.ca), Canada.

<!-- :wrap=soft:maxLineLen=80: -->