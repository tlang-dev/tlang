package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangType}
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangArrayValue(context: Option[ContextContent], var `type`: Option[LangType] = None, var params: Option[List[AstTmplNode]]) extends LangPrimitiveValue[LangArrayValue] {
  //  override def deepCopy(): LangArrayValue = LangArrayValue(context,
  //    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
  //    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[AstTmplNode])) else None)


  override def getElement: LangArrayValue = this

  override def getType: Type = LangArrayValue.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangArrayValue.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "tType",
      //        if (`type`.isDefined) Some(`type`.get.toEntity) else None,
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "params",
      //        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      )
    ))
  )


  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangArrayValue.model
}

object LangArrayValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("tType")),
    BuildAstTmpl.createModelAttrNull(None, Some("params")),
  )))
}
