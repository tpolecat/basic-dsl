Embedded BASIC
==============

This is an embedded DSL and pure monadic interpreter for the execrable BASIC language. It's useless but kind of interesting. Still a work in progress, but you can write real programs as things stand. The `examples/` directory has a playable game that I typed in from a book written in 1978. 

Quick Start
-----------

You can run the examples via `sbt run`. Other than that, check the code out I guess.

An Example
------------------

This is Scala code.

```scala
import org.tpolecat.basic.BASIC

object HelloWorld extends App {

  val b = new BASIC {

    10 PRINT "HELLO WORLD"
    20 INPUT "WHAT IS YOUR NAME?" AS N$
    30 INPUT "HOW MANY TIMES SHALL I PRINT IT?" AS N
    40 IF N <> INT(N) THEN 30
    50 IF N < 1 THEN 30
    60 PRINT "OK THEN:"
    70 FOR I IN 1 TO N
    80 PRINT N$
    90 NEXT I
    95 PRINT "BYE."

  }

  b.run.unsafePerformIO()

}
```

Known Issues
------------

Where shall I begin? First of all, this thing runs BASIC, which can be considered a bug by definition. By other than that:

* The embedded DSL can be improved to support more syntax, and make existing syntax look more natural. This is boring so I haven't spent much time on it.
* The implementation is mostly pure, but there's a `var` in `BASIC.scala` and I'm not sure how to get rid of it without messing up the surface syntax.
* Many many other things I haven't thought about.

