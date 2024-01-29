package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.func.LangAnnotationParam
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangAnnotation(context: Option[ContextContent], var name: LangID, var values: Option[List[LangAnnotationParam]]) extends LangContent[LangAnnotation] with AstContext {
  override def deepCopy(): LangAnnotation = LangAnnotation(context, name.deepCopy().asInstanceOf[LangID],
    if (values.isDefined) Some(values.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangAnnotation]): Int = 0

  override def getElement: LangAnnotation = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAnnotation.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrNull(context, "values",
        if (values.isDefined) Some(ArrayValue(context, Some(values.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      )
    ))
  )

  override def toModel: ModelSetEntity = LangAnnotation.model
}

object LangAnnotation {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("name"), ModelSetType(None, LangID.name)),
    ModelSetAttribute(None, Some("values"), ModelSetType(None, NullValue.name)),
  )))
}