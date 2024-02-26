package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.func.LangAnnotationParam
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent, TmplID}

case class LangAnnotation(context: Null, var name: TmplID, var values: Option[List[LangAnnotationParam]]) extends LangContent[LangAnnotation] with AstContext {
//  override def deepCopy(): LangAnnotation = LangAnnotation(context, name.deepCopy().asInstanceOf[TmplID],
//    if (values.isDefined) Some(values.get.map(_.deepCopy())) else None)

  override def getContext: Null = context

  override def getElement: LangAnnotation = this

  override def getType: Type = LangAnnotation.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAnnotation.modelName)),
    Some(List(
      BuildLang.createAttrEntity(context, "name", name.toEntity),
//      BuildLang.createAttrNull(context, "values",
//        if (values.isDefined) Null.of(ArrayValue(context, Some(values.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else Null.empty(),
//        None
//      )
    ))
  )

//  override def toModel: ModelSetEntity = LangAnnotation.model
}

object LangAnnotation {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TmplID.TYPE)),
    ModelSetAttribute(Null.empty(), Some("values"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}