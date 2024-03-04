package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class CallObject(context: Option[ContextContent], statements: List[CallObjectType], var path: Option[String] = None) extends ComplexValueStatement[CallObject] with AstTmplNode {

  override def getType: Type = CallObject.getType

  override def toEntity: AstEntity = AstEntity(context,
    Some(CallObject.model),
    Some(List(
    ))
  )

  override def getContext: Option[ContextContent] = context

  override def getElement: CallObject = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = CallObject.model
}

object CallObject extends TLangType {

  override def getType: Type = ManualType(getClass.getPackageName, name)

  override def getValueType: ValueType = ObjType(None, Some("TLang"), getType)

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrArray(None, Some("calls")),
  )))
}
