package dev.tlang.tlang.generator

import scala.collection.mutable.ListBuffer

case class Seq(var seq: String = "", var child: Option[Seq] = None, var opening: Option[Seq] = None, var closing: Option[Seq] = None, children: ListBuffer[Seq] = ListBuffer.empty, var blockName: String = "") {

  def ->(seq: String): Seq = {
    this.seq = seq
    this
  }

  def ->(seq: Seq): Seq = {
    child = Some(seq)
    this
  }

  def +=(seq: Seq): Seq = {
    child = Some(seq)
    seq
  }

  def +=(seq: String): Seq = {
    val newSeq = Seq(seq)
    child = Some(newSeq)
    newSeq
  }

  def ->(seqs: Iterable[Seq]): Seq = {
    if (seqs.nonEmpty) {
      var lastSeq = Seq()
      seqs.foreach(seq => {
        lastSeq.child = Some(seq)
        lastSeq = lastSeq.child.get
      })
      lastSeq
    } else {
      this
    }
  }

  def ->(seqs: Seq*): Seq = {
    if (seqs.nonEmpty) {
      var lastSeq = Seq()
      seqs.foreach(seq => {
        lastSeq.child = Some(seq)
        lastSeq = lastSeq.child.get
      })
      lastSeq
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
    child.foreach(child => str ++= child.toString)
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