package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl._
import tlang.core.Type
import tlang.internal.{ContextContent, TmplNode}

case class StyleSetAttribute(context: Option[ContextContent], name: Option[TmplID], value: AstTmplNode) extends StyleAttribute[StyleSetAttribute] {
  override def toEntity: AstEntity = AstEntity(context,
    Some(StyleSetAttribute.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "name",
      //        if (name.isDefined) Null.of(name.get.toEntity) else Null.empty(),
      //        None
      //      ),
      //      BuildLang.createAttrEntity(context, "value", value.toEntity)
    ))
  )

  override def getElement: StyleSetAttribute = this

  override def getType: Type = StyleSetAttribute.modelName

  //  override def deepCopy(): StyleSetAttribute = StyleSetAttribute(context,
  //    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
  //    value.deepCopy().asInstanceOf[AstTmplNode]
  //  )

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = StyleSetAttribute.model
}

object StyleSetAttribute {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(StyleModel.styleModel), None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("name")),
    BuildAstTmpl.createModelAttrEntity(None, Some("value"), TmplNode.TYPE),
  )))
}
