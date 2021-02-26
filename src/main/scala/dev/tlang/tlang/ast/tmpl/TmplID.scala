package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.common.call.CallObject

sealed trait TmplID extends DeepCopy {

  override def toString: String = {
    this match {
      case iD: TmplStringID => iD.id
      case interId: TmplInterpretedID => interId.pre.getOrElse("") + "${uninterpreted}" + interId.post.getOrElse("")
      case _: TmplBlockID => "${uninterpretedBlock}"
    }
  }
}

case class TmplInterpretedID(pre: Option[String] = None, call: CallObject, post: Option[String] = None) extends TmplID {
  override def deepCopy(): TmplInterpretedID = TmplInterpretedID(
    if (pre.isDefined) Some(new String(pre.get)) else None,
    call.copy(),
    if (post.isDefined) Some(new String(post.get)) else None)
}

case class TmplStringID(id: String) extends TmplID {
  override def deepCopy(): TmplStringID = TmplStringID(new String(id))
}

case class TmplBlockID(block: TmplBlock) extends TmplID {
  override def deepCopy(): TmplBlockID = TmplBlockID(block.deepCopy())
}
