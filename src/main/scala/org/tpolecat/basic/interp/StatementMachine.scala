package org.tpolecat.basic.interp

import scalaz.syntax.monad._
import scalaz.syntax.std.boolean._
import scalaz.effect.IO
import scalaz.effect.IO._
import org.tpolecat.basic.ast.Statements

trait StatementMachine extends ExprMachine with Statements {

  // Execute a single statement
  def step: Op[Unit] = gets(s => s.p(s.pc)) >>= {

    // Trivial Statements
    case Goto(e)         => evalInt(e) >>= jmp
    case Gosub(e)        => evalInt(e) >>= jsr
    case Next(s: Symbol) => nextFor(s)
    case Return          => ret
    case End             => end

    case Let(k, e) => for {
      v <- eval(e)
      _ <- bind(k)(v)
      _ <- advance
    } yield ()

    case Print(e) => for {
      v <- eval(e)
      _ <- putV(v).liftIO[Op]
      _ <- advance
    } yield ()

    case Input(p, k) => for {
      _ <- putV(VString(p)).liftIO[Op]
      v <- getV(k.isStringVariable).liftIO[Op]
      _ <- bind(k)(v)
      _ <- advance
    } yield ()

    case If(e1, e2) => for {
      b <- evalBool(e1)
      _ <- if (b) evalInt(e2) >>= jmp else advance
    } yield ()

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

  private def getV(isString: Boolean): IO[Variant] = for {
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

  private def putV(v: Variant): IO[Unit] = IO(v) map {
    case VString(s) => s
    case VNumber(e) => e.fold(_.toString, _.toString)
  } >>= putStrLn

}

