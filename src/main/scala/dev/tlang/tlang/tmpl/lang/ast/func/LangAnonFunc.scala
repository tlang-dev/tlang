package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.{LangExprContent, LangExpression, LangModel}
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangAnonFunc(context: Option[ContextContent], var curries: Option[List[LangFuncParam]], var content: LangExprContent[_]) extends LangExpression[LangAnonFunc] {

  override def getType: Type = LangAnonFunc.modelName

  override def getContext: Option[ContextContent] = context

  //  override def deepCopy(): LangAnonFunc = LangAnonFunc(context,
  //    if (curries.isDefined) Some(curries.get.map(curry => curry.deepCopy())) else None,
  //    content.deepCopy().asInstanceOf[LangExprContent[_]])


  override def toEntity: AstEntity = AstEntity(context,
    Some(LangAnonFunc.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "curries",
      //        if (curries.isDefined) Some(ArrayValue(context, Some(curries.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      //      BuildAstTmpl.createAttrEntity(context, "content", content.toEntity),
    ))
  )

  override def getElement: LangAnonFunc = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangAnonFunc.model
}

object LangAnonFunc {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("curries")),
//    BuildAstTmpl.createModelAttrEntity(None, Some("content"), LangExprContent.model),
  )))
}
