package dev.tlang.tlang.parser

import scala.collection.mutable.ListBuffer

case class Seq(seq: String, var children: ListBuffer[Seq] = ListBuffer.empty, pos: Pos)
