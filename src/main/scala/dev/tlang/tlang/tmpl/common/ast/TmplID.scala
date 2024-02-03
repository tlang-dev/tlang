package dev.tlang.tlang.tmpl.common.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.lang.ast.{LangBlock, LangModel}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

sealed trait TmplID extends TmplNode[TmplID] {

  override def toString: String = {
    this match {
      case iD: TmplStringID => iD.id
      case interId: TmplInterpretedID => interId.pre.getOrElse("") + "${uninterpreted}" + interId.post.getOrElse("")
      case replId: TmplReplacedId => replId.pre.getOrElse("") + replId.node.toString + replId.post.getOrElse("")
      case _: LangBlockID => "${uninterpretedBlock}"
    }
  }
}

object TmplID {
  val name: String = this.getClass.getSimpleName.replace("$", "")
}

case class TmplInterpretedID(context: Option[ContextContent], pre: Option[String] = None, call: CallObject, post: Option[String] = None) extends TmplID {
  override def deepCopy(): TmplInterpretedID = TmplInterpretedID(context,
    if (pre.isDefined) Some(new String(pre.get)) else None,
    call.copy(),
    if (post.isDefined) Some(new String(post.get)) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplID]): Int = 0

  override def getElement: TmplID = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplInterpretedID.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = TmplInterpretedID.model
}

object TmplInterpretedID {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
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

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplReplacedId.name)),
    Some(List(BuildLang.createAttrStr(context, "value", pre.getOrElse("") + node.toString + post.getOrElse(""))))
  )

  override def toModel: ModelSetEntity = TmplReplacedId.model
}

object TmplReplacedId {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}

case class TmplStringID(context: Option[ContextContent], id: String) extends TmplID {
  override def deepCopy(): TmplStringID = TmplStringID(context, new String(id))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplID]): Int = 0

  override def getElement: TmplID = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplStringID.name)),
    Some(List(
      BuildLang.createAttrStr(context, "value", id)
    ))
  )

  override def toModel: ModelSetEntity = TmplStringID.model
}

object TmplStringID {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}

case class LangBlockID(context: Option[ContextContent], block: LangBlock) extends TmplID {
  override def deepCopy(): LangBlockID = LangBlockID(context, block.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplID]): Int = 0

  override def getElement: TmplID = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangBlockID.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangBlockID.model
}

object LangBlockID {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
