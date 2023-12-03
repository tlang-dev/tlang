package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.{EntityValue, TLangType}
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.tmpl.TmplBlock

case class TmplBlockAsValue(astContext: Option[ContextContent], var block: TmplBlock[_], context: Context) extends TmplNode[TmplBlock[_]] {
  override def getElement: TmplBlock[_] = this.block

  override def getType: String = TmplBlockAsValue.getType

  override def compareTo(value: Value[TmplBlock[_]]): Int = 0

  override def deepCopy(): TmplBlockAsValue = new TmplBlockAsValue(astContext, block.deepCopy().asInstanceOf[TmplBlock[_]], context)

  override def getContext: Option[ContextContent] = astContext

  override def toEntity: EntityValue = EntityValue(astContext, None, Some(List()))

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}

object TmplBlockAsValue extends TLangType {
  override def getType: String = "LangBlock"

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}