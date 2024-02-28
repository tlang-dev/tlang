package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{ContextContent, TmplID}

case class LangAttribute(context: Option[ContextContent], var attr: Option[TmplID], var `type`: Option[LangType], var value: LangOperation) extends AstTmplNode {
  //  override def deepCopy(): LangAttribute = LangAttribute(context,
  //    if (attr.isDefined) Some(attr.get.getElement.deepCopy().asInstanceOf[TmplID]) else None,
  //    if (`type`.isDefined) Some(`type`.get.getElement.deepCopy()) else None,
  //    value.deepCopy()
  //  )

  override def toEntity: AstEntity = AstEntity(context,
    Some(LangAttribute.model),
    Some(List(
      //      BuildAstTmpl.createAttrNull(context, "attr",
      //        attr
      //      ),
      //      BuildAstTmpl.createAttrNull(context, "tType",
      //        `type`,
      //      ),
      BuildAstTmpl.createAttrEntity(context, "value", Some(LangOperation.model.getType), value.toEntity),
    ))
  )

  //  override def toModel: ModelSetEntity = LangAttribute.model

  override def getElement: LangAttribute = this

  override def getType: Type = LangAttribute.modelName

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangAttribute.model
}

object LangAttribute {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("attr")),
    BuildAstTmpl.createModelAttrNull(None, Some("tType")),
    BuildAstTmpl.createModelAttrEntity(None, Some("value"), LangOperation.modelType),
  )))
}
