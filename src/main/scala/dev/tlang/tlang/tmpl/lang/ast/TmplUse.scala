package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang.createArray

case class TmplUse(context: Option[ContextContent], var parts: List[TmplID], var alias: Option[TmplID] = None) extends TmplNode[TmplUse] with DeepCopy {
  override def deepCopy(): TmplUse = TmplUse(context, parts.map(_.deepCopy().asInstanceOf[TmplID]),
    if (alias.isDefined) Some(alias.get.deepCopy().asInstanceOf[TmplID]) else None)

  override def compareTo(value: Value[TmplUse]): Int = 0

  override def getElement: TmplUse = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = {
    EntityValue(context,
      Some(ObjType(context, None, TmplLangAst.langUse.name)),
      Some(List(
        createArray(context, "parts", parts.map(part => part.toEntity))
      )
      ))
  }

  override def toModel: ModelSetEntity = TmplUse.model
}

object TmplUse {

  val model: ModelSetEntity = ModelSetEntity(None, "LangUse", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}

