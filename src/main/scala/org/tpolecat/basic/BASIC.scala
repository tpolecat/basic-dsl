package org.tpolecat.basic

import scalaz._
import scalaz.syntax.monad._
import scala.annotation.tailrec
import scala.collection.immutable.SortedMap
import org.tpolecat.basic.dsl.Embedded
import org.tpolecat.basic.interp.StatementMachine
import scalaz.effect.IO
import scalaz.effect.IO._

trait BASIC extends StatementMachine with Embedded {

  // This is lame; we build up the program line by line as the DSL is evaluated.
  private var prog: Program = SortedMap()
  def addStatement(pc: Int, s: Statement): Unit = prog = prog + (pc -> s)

  // Construct our IO action that executes the program.
  private def go(r: Running): IO[Halted] =
    step(r).run >>= (_.fold(IO(_), p => go(p._1)))

  // Construct our initail state and IO action. 
  def run: IO[Unit] = prog.keys.headOption.fold(ioUnit) { n =>
    for {
      h <- go(Running(prog, n))
      _ <- h.error.fold(ioUnit)(e => putStrLn(e.toString))
    } yield ()
  }

  // Unsafe execution, BASIC-style!
  def RUN = run.unsafePerformIO

}


