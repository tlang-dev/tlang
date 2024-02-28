package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangParam}
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{ContextContent, TmplNode}

case class LangFuncParam(context: Option[ContextContent], params: Option[List[LangParam]], var `type`: String) extends AstTmplNode {

  override def getElement: LangFuncParam = this

  override def getType: Type = LangFuncParam.modelName

  override def getContext: Option[ContextContent] = context

  //  override def deepCopy(): LangFuncParam = LangFuncParam(context,
  //    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None,
  //    `type`
  //  )

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangFuncParam.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "params",
      //        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      BuildAstTmpl.createAttrStr(context, "tType", `type`),
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangFuncParam.model
}

object LangFuncParam {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("params")),
    BuildAstTmpl.createModelAttrStr(None, Some("tType")),
  )))
}
