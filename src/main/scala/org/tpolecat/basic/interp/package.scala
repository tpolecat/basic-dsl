package org.tpolecat.basic

package object interp {

  // TODO: fix this up
  implicit class PimpSymbol(s: Symbol) {
    def isStringVariable = s.name.endsWith("$")
  }

}