package org.tpolecat.basic.ast

import scala.collection.immutable.SortedMap

trait Base {
	type Statement
	type Program = SortedMap[Int, Statement]
}