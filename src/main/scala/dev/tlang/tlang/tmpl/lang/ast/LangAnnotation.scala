package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.func.LangAnnotationParam
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{AstContext, ContextContent, TmplID}

case class LangAnnotation(context: Null[ContextContent], var name: TmplID, var values: Option[List[LangAnnotationParam]]) extends LangContent[LangAnnotation] with AstContext {
  override def deepCopy(): LangAnnotation = LangAnnotation(context, name.deepCopy().asInstanceOf[TmplID],
    if (values.isDefined) Some(values.get.map(_.deepCopy())) else None)

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[LangAnnotation]): Int = 0

  override def getElement: LangAnnotation = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAnnotation.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrNull(context, "values",
        if (values.isDefined) Null.of(ArrayValue(context, Some(values.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else Null.empty(),
        None
      )
    ))
  )

  override def toModel: ModelSetEntity = LangAnnotation.model
}

object LangAnnotation {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TmplID.name.toString)),
    ModelSetAttribute(Null.empty(), Some("values"), ModelSetType(Null.empty(), NullValue.name)),
  )))
}