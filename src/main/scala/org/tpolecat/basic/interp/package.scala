package org.tpolecat.basic

package object interp {

  implicit class PimpSymbol(s: Symbol) {
    def isStringVariable = s.name.endsWith("$")
  }

}