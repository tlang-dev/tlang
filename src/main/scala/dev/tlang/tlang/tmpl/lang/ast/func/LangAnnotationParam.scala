package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl._
import tlang.core.Type
import tlang.internal.ContextContent

case class LangAnnotationParam(context: Option[ContextContent], var name: Option[TmplID], var value: LangValueType[_]) extends AstTmplNode {
  //  override def deepCopy(): LangAnnotationParam =
  //    LangAnnotationParam(context,
  //      if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
  //      value.deepCopy().asInstanceOf[LangValueType[_]])

  override def getContext: Option[ContextContent] = context

  override def getElement: LangAnnotationParam = this

  override def getType: Type = LangAnnotationParam.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangAnnotationParam.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "name",
      //        if (name.isDefined) Null.of(name.get.toEntity) else Null.empty(),
      //        None
      //      ),
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangAnnotationParam.model
}

object LangAnnotationParam {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("name")),
  )))
}
