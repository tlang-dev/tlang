package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.{EntityValue, TLangType}
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.Context

case class TmplBlockAsValue(astContext: Option[ContextContent], var block: LangBlock, context: Context) extends TmplNode[LangBlock] {
  override def getElement: LangBlock = this.block

  override def getType: String = TmplBlockAsValue.getType

  override def compareTo(value: Value[LangBlock]): Int = 0

  override def deepCopy(): TmplBlockAsValue = new TmplBlockAsValue(astContext, block.deepCopy(), context)

  override def getContext: Option[ContextContent] = astContext

  override def toEntity: EntityValue = EntityValue(astContext, None, Some(List()))
}

object TmplBlockAsValue extends TLangType {
  override def getType: String = "LangBlock"

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)
}
