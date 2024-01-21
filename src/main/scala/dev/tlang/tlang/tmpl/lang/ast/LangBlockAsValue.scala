package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.{EntityValue, TLangType}
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.tmpl.AnyTmplBlock

case class LangBlockAsValue(astContext: Option[ContextContent], var block: AnyTmplBlock[_], context: Context) extends LangNode[AnyTmplBlock[_]] {
  override def getElement: AnyTmplBlock[_] = this.block

  override def getType: String = LangBlockAsValue.getType

  override def compareTo(value: Value[AnyTmplBlock[_]]): Int = 0

  override def deepCopy(): LangBlockAsValue = new LangBlockAsValue(astContext, block.deepCopy().asInstanceOf[AnyTmplBlock[_]], context)

  override def getContext: Option[ContextContent] = astContext

  override def toEntity: EntityValue = EntityValue(astContext,
    Some(ObjType(astContext, None, LangBlockAsValue.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = LangBlockAsValue.model
}

object LangBlockAsValue extends TLangType {
  override def getType: String = "LangBlock"

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)

  val name: String = getType

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
