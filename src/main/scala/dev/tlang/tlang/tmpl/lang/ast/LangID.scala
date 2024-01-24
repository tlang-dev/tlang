package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

sealed trait LangID extends LangNode[LangID] {

  override def toString: String = {
    this match {
      case iD: LangStringID => iD.id
      case interId: LangInterpretedID => interId.pre.getOrElse("") + "${uninterpreted}" + interId.post.getOrElse("")
      case replId: LangReplacedId => replId.pre.getOrElse("") + replId.node.toString + replId.post.getOrElse("")
      case _: LangBlockID => "${uninterpretedBlock}"
    }
  }
}

object LangID {
  val name: String = this.getClass.getSimpleName.replace("$", "")
}

case class LangInterpretedID(context: Option[ContextContent], pre: Option[String] = None, call: CallObject, post: Option[String] = None) extends LangID {
  override def deepCopy(): LangInterpretedID = LangInterpretedID(context,
    if (pre.isDefined) Some(new String(pre.get)) else None,
    call.copy(),
    if (post.isDefined) Some(new String(post.get)) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangID]): Int = 0

  override def getElement: LangID = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangInterpretedID.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangInterpretedID.model
}

object LangInterpretedID {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}

/**
 * Replace TmplInterpretedId when CallObject is resolved
 * Pre and post don't work with list because it does not really make sense,
 * so only one node is accepted
 */
case class LangReplacedId(context: Option[ContextContent], pre: Option[String] = None, node: LangNode[_], post: Option[String] = None) extends LangID {
  override def deepCopy(): LangReplacedId = LangReplacedId(context,
    if (pre.isDefined) Some(new String(pre.get)) else None,
    node.deepCopy().asInstanceOf[LangNode[_]],
    if (post.isDefined) Some(new String(post.get)) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangID]): Int = 0

  override def getElement: LangID = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangReplacedId.name)),
    Some(List(BuildLang.createAttrStr(context, "value", pre.getOrElse("") + node.toString + post.getOrElse(""))))
  )

  override def toModel: ModelSetEntity = LangReplacedId.model
}

object LangReplacedId {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}

case class LangStringID(context: Option[ContextContent], id: String) extends LangID {
  override def deepCopy(): LangStringID = LangStringID(context, new String(id))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangID]): Int = 0

  override def getElement: LangID = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangStringID.name)),
    Some(List(
      BuildLang.createAttrStr(context, "value", id)
    ))
  )

  override def toModel: ModelSetEntity = LangStringID.model
}

object LangStringID {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}

case class LangBlockID(context: Option[ContextContent], block: LangBlock) extends LangID {
  override def deepCopy(): LangBlockID = LangBlockID(context, block.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangID]): Int = 0

  override def getElement: LangID = this

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
