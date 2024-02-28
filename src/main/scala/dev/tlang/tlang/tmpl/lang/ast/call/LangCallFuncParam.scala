package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{ContextContent, TmplNode}

case class LangCallFuncParam(context: Option[ContextContent], var params: Option[List[TmplNode[_]]]) extends TmplNode[LangCallFuncParam] {

  override def getType: Type = LangCallFuncParam.modelName

  override def getContext: Option[ContextContent] = context

  //  override def deepCopy(): LangCallFuncParam = LangCallFuncParam(
  //    context,
  //    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None,
  //  )

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangCallFuncParam.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "params",
      //        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      )
    ))
  )

  override def getElement: LangCallFuncParam = this
}

object LangCallFuncParam {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("params")),
  )))
}
