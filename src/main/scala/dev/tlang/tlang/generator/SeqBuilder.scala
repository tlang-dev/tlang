package dev.tlang.tlang.generator

import dev.tlang.tlang.generator.SeqBuilder.findLastChild

class SeqBuilder {

  private val seq = Seq();

  private var lastHeader: Option[Seq] = None
  private var lastContent: Option[Seq] = None
  private var lastBottom: Option[Seq] = None

  def head(seq: Seq): SeqBuilder = {
    if (lastHeader.isDefined) {
      lastHeader.get.child = Some(seq)
      lastHeader = Some(findLastChild(seq))
    } else {
      this.seq.opening = Some(seq)
      lastHeader = Some(findLastChild(seq))
    }
    this
  }

  def +=(seq: Seq): SeqBuilder = {
    if (lastContent.isDefined) {
      lastContent.get.child = Some(seq)
      lastContent = Some(findLastChild(seq))
    } else {
      this.seq.child = Some(seq)
      lastContent = Some(findLastChild(seq))
    }
    this
  }

  def +=(seq: String): SeqBuilder = {
    if (lastContent.isDefined) {
      val newSeq = Seq(seq)
      lastContent.get.child = Some(newSeq)
      lastContent = Some(findLastChild(newSeq))
    } else {
      this.seq.child = Some(Seq(seq))
      lastContent = this.seq.child
    }
    this
  }

  def ++=(seq: Seq): SeqBuilder = {
    this.seq.children += seq
    this
  }

  def bottom(seq: Seq): SeqBuilder = {
    if (lastBottom.isDefined) {
      lastBottom.get.child = Some(seq)
      lastBottom = Some(findLastChild(seq))
    } else {
      this.seq.closing = Some(seq)
      lastBottom = Some(findLastChild(seq))
    }
    this
  }

  def setBlockName(name: String): SeqBuilder = {
    this.seq.blockName = name
    this
  }

  def setSeq(seq: String): SeqBuilder = {
    this.seq.seq = seq
    this
  }

  def build(): Seq = {
    seq
  }


}

object SeqBuilder {
  def build(seqs: Seq*): Seq = {
    val seq = seqs.head
    var current = findLastChild(seq)
    seqs.slice(1, seqs.length).foreach(aSeq => {
      current.child = Some(aSeq)
      current = findLastChild(aSeq)
    })
    seq
  }

  def findLastChild(seq: Seq): Seq = {
    if (seq.child.isDefined) return findLastChild(seq.child.get)
    seq
  }
}