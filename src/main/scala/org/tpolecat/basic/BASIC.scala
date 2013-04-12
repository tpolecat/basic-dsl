package org.tpolecat.basic

import scalaz._
import scala.annotation.tailrec
import scala.collection.immutable.SortedMap
import org.tpolecat.basic.dsl.Embedded
import org.tpolecat.basic.interp.StatementMachine

trait BASIC extends StatementMachine with Embedded {

  private var prog:Program = SortedMap()
  def addStatement(pc:Int, s:Statement):Unit = prog = prog + (pc -> s)
  
  @tailrec
  private def go(r: Running): Halted =
    step(r) match { // N.B. fold works but isn't tail recursive
      case -\/(a)      => a
      case \/-((r, _)) => go(r)
    }

  def RUN = run()

  /** Run the program and report errors. */
  def run(): Unit =
    for {
      n <- prog.keys.headOption
      e <- go(Running(prog, n)).error
    } System.err.println(e)

}


