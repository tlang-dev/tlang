package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl._
import tlang.core.Type
import tlang.internal.ContextContent

case class LangSetAttribute(context: Option[ContextContent], var name: Option[TmplID], var value: LangOperation) extends AstTmplNode {
  //  override def deepCopy(): LangSetAttribute = LangSetAttribute(context,
  //    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None, value.deepCopy())


  override def getElement: LangSetAttribute = this

  override def getType: Type = LangSetAttribute.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangSetAttribute.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "name",
      //        name,
      //        None
      //      ),
      //      BuildLang.createAttrEntity(context, "value", value.toEntity),
    )))

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangSetAttribute.model
}

object LangSetAttribute {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("name")),
    BuildAstTmpl.createModelAttrEntity(None, Some("value"), LangOperation.modelType),
  )))
}
