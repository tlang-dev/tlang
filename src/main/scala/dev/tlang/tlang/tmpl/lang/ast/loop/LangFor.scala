package dev.tlang.tlang.tmpl.lang.ast.loop

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.{LangExprContent, LangExpression, LangModel}
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl, TmplID}
import tlang.core.Type
import tlang.internal.ContextContent

case class LangFor(context: Option[ContextContent], var variable: TmplID, var start: Option[LangOperation], forType: ForType.ForType, var cond: LangOperation, var content: LangExprContent[_]) extends LangExpression[LangFor] {
  //  override def deepCopy(): LangFor = LangFor(context,
  //    variable.deepCopy().asInstanceOf[TmplID],
  //    if (start.isDefined) Some(start.get.deepCopy()) else None,
  //    forType, cond.deepCopy(), content.deepCopy().asInstanceOf[LangExprContent[_]])


  override def getElement: LangFor = this

  override def getType: Type = LangFor.modelName


  override def toEntity: AstEntity = AstEntity(context,
    Some(LangFor.model),
    Some(List(
      //      BuildAstTmpl.createAttrEntity(context, "variable", variable.toEntity),
      //      BuildLang.createAttrNull(context, "start",
      //        if (start.isDefined) Some(start.get.toEntity) else None,
      //        None
      //      ),
      BuildAstTmpl.createAttrStr(context, "forType", ForType.value(forType)),
      //      BuildLang.createAttrEntity(context, "cond", cond.toEntity),
      //      BuildAstTmpl.createAttrEntity(context, "content", content.toEntity),
    ))
  )

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangFor.model
}

object LangFor {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrTmplID(None, Some("variable")),
    BuildAstTmpl.createModelAttrNull(None, Some("start")),
    BuildAstTmpl.createModelAttrStr(None, Some("forType")),
    BuildAstTmpl.createModelAttrEntity(None, Some("cond"), LangOperation.modelType),
    BuildAstTmpl.createModelAttrEntity(None, Some("content"), LangExprContent.modelName),
  )))
}

object ForType extends Enumeration {
  type ForType = Value
  val IN, TO, UNTIL = Value

  def value(forType: ForType): String = {
    forType match {
      case IN => "IN"
      case TO => "TO"
      case UNTIL => "UNTIL"
    }
  }
}
