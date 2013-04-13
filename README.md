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

How does the interpreter work?
-------------------------

_This explanation is mostly so I can remember how the monad stack works._

The interpreter is a state machine with a running state, appropriately enough, of type `Running`. This is a case class that holds the program, the program counter, variable bindings, loop states, and so on.

An operation `Op[+A]` that depends on this state and returns a value of type `A` has type `Running => (Running, A)`, or `State[Running,A]`. So, this is great but it doesn't deal with failure: what if we can't return a value of type `A`, for example?

To handle this we have to widen our return type to `Running => Either[Halted, (Running,A)]` or `Running => Halted \/ (Running,A)`, also known as `StateT[({type λ[+α] = Halted \/ α})#λ, Running, A]`. We can lift our old state operations into this new `Op` type by declaring a type alias `Answer[A] = Halted \/ A` and then saying `action.lift[Answer]`, and we can reduce `Op[A]` to `Halted \/ (Running,A)` via `op.run(r).run`.

But to handle IO in a pure way, we don't want to get back a `Halted \/ (Running,A)` back, we want an `IO[Halted \/ (Running,A)]`! To do this we need to expand our type again such that `Op[+A] = StateT[({type λ[+α] = EitherT[IO, Halted, α]})#λ, Running, A]`. We can lift our original state operations in the same way as above (with alias `Answer[+A] = EitherT[IO, Halted, A]`) and given an instance of `MonadIO[Op]` (see code) we can lift `IO[A]` to `Op[A]` via `io.liftIO[Op]`. We can now reduce `Op[A]` to `Halted \/ (Running,A)` via `op.run(r).run.unsafePerformIO`.


Known Issues
------------

Where shall I begin? First of all, this thing runs BASIC, which can be considered a bug by definition. By other than that:

* The embedded DSL can be improved to support more syntax, and make existing syntax look more natural. This is boring so I haven't spent much time on it.
* The implementation is mostly pure, but there's a `var` in `BASIC.scala` and I'm not sure how to get rid of it without messing up the surface syntax.
* Many many other things I haven't thought about.

