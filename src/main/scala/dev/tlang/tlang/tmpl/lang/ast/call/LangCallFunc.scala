package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl, TmplID}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangCallFunc(context: Option[ContextContent], var name: TmplID, var currying: Option[List[LangCallFuncParam]]) extends LangCallObjType[LangCallFunc] {
  //  override def deepCopy(): LangCallFunc = LangCallFunc(context, name.deepCopy().asInstanceOf[TmplID],
  //    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None)


  override def getElement: LangCallFunc = this

  override def getType: Type = LangCallFunc.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangCallFunc.model),
    Some(List(
      //      BuildAstTmpl.createAttrEntity(context, "name", name.toEntity),
      //      BuildLang.createAttrNull(context, "currying",
      //        if (currying.isDefined) Some(ArrayValue(context, Some(currying.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      )
    ))
  )

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangCallFunc.model
}

object LangCallFunc {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrTmplID(None, Some("name")),
    BuildAstTmpl.createModelAttrNull(None, Some("currying")),
  )))
}
