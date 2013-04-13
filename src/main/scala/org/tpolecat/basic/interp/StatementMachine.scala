package org.tpolecat.basic.interp

import scalaz.syntax.monad._
import scalaz.syntax.std.boolean._
import scalaz.effect.IO
import scalaz.effect.IO._
import org.tpolecat.basic.ast.Statements

// high-level statements
trait StatementMachine extends ExprMachine with Statements {

  def getV(isString: Boolean): IO[Variant] = for {
    _ <- putStr("> ")
    s <- readLn
    v <- if (isString)
      IO(VString(s))
    else
      try {
        val d = s.toDouble
        IO(Variant(d))
      } catch {
        case _: NumberFormatException => putStrLn("?REDO") >> getV(isString)
      }
  } yield v

  def putV(v: Variant): IO[Unit] = IO(v) map {
    case VString(s) => s
    case VNumber(e) => e.fold(_.toString, _.toString)
  } >>= putStrLn

  def input(isString: Boolean) = getV(isString).liftIO[Op]
  def output(v: Variant): Op[Unit] = putV(v).liftIO[Op]

  def step: Op[Unit] = gets(s => s.p(s.pc)) >>= {

    case Let(k, e)       => (eval(e) >>= bind(k)) >> advance
    case Print(e)        => (eval(e) >>= output) >> advance
    case Goto(e)         => evalInt(e) >>= jmp
    case Gosub(e)        => evalInt(e) >>= jsr
    case Return          => ret
    case Input(p, k)     => (output(VString(p)) >> input(k.isStringVariable) >>= bind(k)) >> advance
    case If(e1, e2)      => evalBool(e1).ifM(evalInt(e2) >>= jmp, advance)
    case Next(s: Symbol) => nextFor(s)
    case End             => end

    // Setting up a FOR loop has a few steps
    case For(s, e1, e2) =>
      for {
        i <- evalInt(e1)
        j <- evalInt(e2)
        _ <- bind(s)(Variant(i))
        n <- gets(s => s.next(s.pc))
        _ <- initFor(s, n, j)
        _ <- advance
      } yield ()

  }

}

