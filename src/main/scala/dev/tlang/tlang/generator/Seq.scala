package dev.tlang.tlang.generator

import scala.collection.mutable.ListBuffer

case class Seq(var seq: String = "", children: ListBuffer[Seq] = ListBuffer.empty[Seq], var opening: Option[Seq] = None, var closing: Option[Seq] = None, blockName: String = "") {

  def ->(seq: String): Seq = {
    this.seq = seq
    this
  }

  def ->(seq: Seq): Seq = {
    children.addOne(seq)
    this
  }

  def +=(seq: Seq): Seq = {
    children.addOne(seq)
    seq
  }

  def +=(seq: String): Seq = {
    val newSeq = Seq(seq)
    children.addOne(newSeq)
    newSeq
  }

  def ->(seqs: Iterable[Seq]): Seq = {
    if (seqs.nonEmpty) {
      children.addAll(seqs)
      seqs.last
    } else {
      this
    }
  }

  def ->(seqs: Seq*): Seq = {
    if (seqs.nonEmpty) {
      children.addAll(seqs)
      seqs.last
    } else {
      this
    }
  }

  def open(seq: Seq): Seq = {
    if (this.opening.isEmpty)
      this.opening = Some(seq)
    seq
  }

  def close(seq: Seq): Seq = {
    this.closing = Some(seq)
    seq
  }

  override def toString: String = {
    val str = new StringBuilder(seq)
    children.foreach(child => str ++= child.toString)
    str.toString()
  }
}

object Seq {


  def build(seqs: String*): Seq = {
    val seq = Seq(seqs.head)
    var current = seq
    for (i <- 1 until seqs.size) current = current += seqs(i)
    seq
  }

  def add(seq: Seq, seqs: String*): Seq = {
    var current = seq
    for (i <- 0 until seqs.size) current = current += seqs(i)
    current
  }

  def addTo(seq: Seq, seqs: String*): Seq = {
    var current = seq
    for (i <- 0 until seqs.size) current = current += seqs(i)
    seq
  }

  def addToLine(seq: Seq, seqs: Seq*): Seq = {
    var current = seq
    for (i <- 0 until seqs.size) current = current += seqs(i)
    seq
  }
}