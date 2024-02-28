package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{ContextContent, TmplID}

case class LangCallArray(context: Option[ContextContent], var name: TmplID, var elem: LangOperation) extends LangCallObjType[LangCallArray] {
  //  override def deepCopy(): LangCallArray = LangCallArray(context, name.deepCopy().asInstanceOf[TmplID], elem.deepCopy())

  override def getElement: LangCallArray = this

  override def getType: Type = LangCallArray.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangCallArray.model),
    Some(List(
      //      BuildAstTmpl.createAttrEntity(context, "name", name.toEntity),
      //      BuildLang.createAttrEntity(context, "operation", elem.toEntity),
    ))
  )

  override def getContext: Option[ContextContent] = context
}

object LangCallArray {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrTmplID(None, Some("name")),
    BuildAstTmpl.createModelAttrEntity(None, Some("operation"), LangOperation.modelType),
  )))
}
