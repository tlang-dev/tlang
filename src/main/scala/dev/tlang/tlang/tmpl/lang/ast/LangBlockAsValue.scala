package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.tmpl._
import tlang.core.Type
import tlang.internal.ContextContent

case class LangBlockAsValue(astContext: Option[ContextContent], var block: AnyTmplInterpretedBlock[_], context: Context) extends AstTmplNode {

  override def getType: Type = LangBlockAsValue.getType

  //  override def deepCopy(): LangBlockAsValue = new LangBlockAsValue(astContext, block.deepCopy().asInstanceOf[AnyTmplInterpretedBlock[_]], context)

  override def toEntity: AstEntity = AstEntity(astContext,
    Some(LangBlockAsValue.model),
    Some(List(
      BuildAstTmpl.createAttrEntity(astContext, "block", Some(AnyTmplInterpretedBlock.modelType), block.toEntity),
    ))
  )

  //  override def toModel: ModelSetEntity = LangBlockAsValue.model

  override def getContext: Option[ContextContent] = astContext

  override def getElement: LangBlockAsValue = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangBlockAsValue.model
}

object LangBlockAsValue extends TLangType {

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)

  val modelType: Type = getType

  val model: AstModel = AstModel(None, modelType, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrEntity(None, Some("block"), AnyTmplInterpretedBlock.modelType),
  )))

  override def getType: Type = modelType
}
