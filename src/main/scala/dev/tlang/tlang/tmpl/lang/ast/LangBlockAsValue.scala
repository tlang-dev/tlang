package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.{EntityValue, TLangType}
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type, Value}
import tlang.internal.{ContextContent, TmplNode}

case class LangBlockAsValue(astContext: Null, var block: AnyTmplInterpretedBlock[_], context: Context) extends TmplNode[LangBlockAsValue] with Value {
  override def getValue: Value = this

  override def getType: Type = LangBlockAsValue.getType

//  override def deepCopy(): LangBlockAsValue = new LangBlockAsValue(astContext, block.deepCopy().asInstanceOf[AnyTmplInterpretedBlock[_]], context)

  override def toEntity: EntityValue = EntityValue(astContext,
    Some(ObjType(astContext, None, LangBlockAsValue.modelType)),
    Some(List(
      BuildLang.createAttrEntity(astContext, "block", block.toEntity),
    ))
  )

//  override def toModel: ModelSetEntity = LangBlockAsValue.model

  override def getContext: Null = astContext

  override def getElement: LangBlockAsValue = this
}

object LangBlockAsValue extends TLangType {

  override def getValueType: ValueType = ObjType(Null.empty(), Some("TLang"), getType)

  val modelType: Type = getType

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelType, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("block"), ModelSetType(Null.empty(), AnyTmplInterpretedBlock.modelType)),
  )))

  override def getType: Type = modelType
}
