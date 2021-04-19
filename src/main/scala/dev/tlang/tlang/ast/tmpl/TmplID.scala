package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

sealed trait TmplID extends TmplNode[TmplID] {

  override def toString: String = {
    this match {
      case iD: TmplStringID => iD.id
      case interId: TmplInterpretedID => interId.pre.getOrElse("") + "${uninterpreted}" + interId.post.getOrElse("")
      case replId: TmplReplacedId => replId.pre.getOrElse("") + replId.node.toString + replId.post.getOrElse("")
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

  override def compareTo(value: Value[TmplID]): Int = 0

  override def getElement: TmplID = this

  override def getType: String = getClass.getName
}

/**
 * Replace TmplInterpretedId when CallObject is resolved
 * Pre and post don't work with list because it does not really make sense,
 * so only one node is accepted
 */
case class TmplReplacedId(context: Option[ContextContent], pre: Option[String] = None, node: TmplNode[_], post: Option[String] = None) extends TmplID {
  override def deepCopy(): TmplReplacedId = TmplReplacedId(context,
    if (pre.isDefined) Some(new String(pre.get)) else None,
    node.deepCopy().asInstanceOf[TmplNode[_]],
    if (post.isDefined) Some(new String(post.get)) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplID]): Int = 0

  override def getElement: TmplID = this

  override def getType: String = getClass.getName
}

case class TmplStringID(context: Option[ContextContent], id: String) extends TmplID {
  override def deepCopy(): TmplStringID = TmplStringID(context, new String(id))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplID]): Int = 0

  override def getElement: TmplID = this

  override def getType: String = getClass.getName
}

case class TmplBlockID(context: Option[ContextContent], block: TmplBlock) extends TmplID {
  override def deepCopy(): TmplBlockID = TmplBlockID(context, block.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplID]): Int = 0

  override def getElement: TmplID = this

  override def getType: String = getClass.getName
}
