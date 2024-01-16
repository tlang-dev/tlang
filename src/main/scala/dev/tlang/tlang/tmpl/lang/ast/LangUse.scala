package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang.createArray

case class LangUse(context: Option[ContextContent], var parts: List[LangID], var alias: Option[LangID] = None) extends LangNode[LangUse] with DeepCopy {
  override def deepCopy(): LangUse = LangUse(context, parts.map(_.deepCopy().asInstanceOf[LangID]),
    if (alias.isDefined) Some(alias.get.deepCopy().asInstanceOf[LangID]) else None)

  override def compareTo(value: Value[LangUse]): Int = 0

  override def getElement: LangUse = this

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

  override def toModel: ModelSetEntity = LangUse.model
}

object LangUse {

  val model: ModelSetEntity = ModelSetEntity(None, "LangUse", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}

