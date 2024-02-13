package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.{EntityValue, TLangType}
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import dev.tlang.tlang.tmpl.{AnyTmplInterpretedBlock, TmplNode}
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class LangBlockAsValue(astContext: Null[ContextContent], var block: AnyTmplInterpretedBlock[_], context: Context) extends TmplNode[AnyTmplInterpretedBlock[_]] {
  override def getElement: AnyTmplInterpretedBlock[_] = this.block

  override def getType: String = LangBlockAsValue.getType

  override def compareTo(value: Value[AnyTmplInterpretedBlock[_]]): Int = 0

  override def deepCopy(): LangBlockAsValue = new LangBlockAsValue(astContext, block.deepCopy().asInstanceOf[AnyTmplInterpretedBlock[_]], context)

  override def toEntity: EntityValue = EntityValue(astContext,
    Some(ObjType(astContext, None, LangBlockAsValue.name)),
    Some(List(
      BuildLang.createAttrEntity(astContext, "block", block.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangBlockAsValue.model
}

object LangBlockAsValue extends TLangType {
  override def getType: String = "LangBlock"

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)

  val name: String = getType

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("block"), ModelSetType(Null.empty(), AnyTmplInterpretedBlock.name)),
  )))
}
