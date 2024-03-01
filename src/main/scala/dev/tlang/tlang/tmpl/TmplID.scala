package dev.tlang.tlang.tmpl

import dev.tlang.tlang.ast.common.call.CallObject
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

sealed trait TmplID extends AstTmplNode {

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

  override def getContext: Option[ContextContent] = context


  override def getElement: TmplID = this

  override def getName: String = getClass.getSimpleName

  override def toEntity: AstEntity = AstEntity(context, None, None)

  override def toModel: AstModel = AstModel(None, getType, None, None, None)

  override def getType: Type = ClassType.of(getClass)
}

/**
 * Replace TmplInterpretedId when CallObject is resolved
 * Pre and post don't work with list because it does not really make sense,
 * so only one node is accepted
 */
case class TmplReplacedId(context: Option[ContextContent], pre: Option[String] = None, node: AstTmplNode, post: Option[String] = None) extends TmplID {

  override def getContext: Option[ContextContent] = context

  override def getElement: TmplID = this

  override def getName: String = getClass.getSimpleName

  override def toEntity: AstEntity = AstEntity(context, None, None)

  override def toModel: AstModel = AstModel(None, getType, None, None, None)

  override def getType: Type = ClassType.of(getClass)

}

case class TmplStringID(context: Option[ContextContent], id: String) extends TmplID {

  override def getContext: Option[ContextContent] = context


  override def getElement: TmplID = this

  override def getName: String = getClass.getSimpleName

  override def toEntity: AstEntity = AstEntity(context, None, None)

  override def toModel: AstModel = AstModel(None, getType, None, None, None)

  override def getType: Type = ClassType.of(getClass)
}

case class TmplBlockID(context: Option[ContextContent], block: AstAnyTmplBlock) extends TmplID {

  override def getContext: Option[ContextContent] = context


  override def getElement: TmplID = this

  override def getName: String = getClass.getSimpleName

  override def toEntity: AstEntity = AstEntity(context, None, None)

  override def toModel: AstModel = AstModel(None, getType, None, None, None)

  override def getType: Type = ClassType.of(getClass)

}