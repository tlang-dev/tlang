package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

sealed trait TmplID extends DeepCopy with AstContext {

  override def toString: String = {
    this match {
      case iD: TmplStringID => iD.id
      case interId: TmplInterpretedID => interId.pre.getOrElse("") + "${uninterpreted}" + interId.post.getOrElse("")
      case _: TmplBlockID => "${uninterpretedBlock}"
    }
  }
}

case class TmplInterpretedID(context: Option[ContextContent], pre: Option[String] = None, call: CallObject, post: Option[String] = None) extends TmplID {
  override def deepCopy(): TmplInterpretedID = TmplInterpretedID(context,
    if (pre.isDefined) Some(new String(pre.get)) else None,
    call.copy(),
    if (post.isDefined) Some(new String(post.get)) else None)

  override def getContext: Option[ContextContent] = context
}

case class TmplStringID(context: Option[ContextContent], id: String) extends TmplID {
  override def deepCopy(): TmplStringID = TmplStringID(context, new String(id))

  override def getContext: Option[ContextContent] = context
}

case class TmplBlockID(context: Option[ContextContent], block: TmplBlock) extends TmplID {
  override def deepCopy(): TmplBlockID = TmplBlockID(context, block.deepCopy())

  override def getContext: Option[ContextContent] = context
}
