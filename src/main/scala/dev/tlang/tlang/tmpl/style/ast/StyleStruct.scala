package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{ContextContent, TmplID}

case class StyleStruct(context: Option[ContextContent], name: Option[TmplID], params: Option[List[StyleAttribute[_]]], attrs: Option[List[StyleAttribute[_]]]) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(StyleStruct.model),
    Some(List(
      //      BuildLang.createAttrNull(context, "name",
      //        if (name.isDefined) Null.of(name.get.toEntity) else Null.empty(),
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "params",
      //        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "attrs",
      //        if (attrs.isDefined) Null.of(ArrayValue(context, Some(attrs.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      )
    ))
  )

  override def getElement: StyleStruct = this

  override def getType: Type = StyleStruct.modelName

  //  override def deepCopy(): StyleStruct = StyleStruct(context,
  //    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
  //    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[StyleAttribute[_]])) else None,
  //    if (attrs.isDefined) Some(attrs.get.map(_.deepCopy().asInstanceOf[StyleAttribute[_]])) else None)

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = StyleStruct.model
}

object StyleStruct {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, None, None, Some(List(
    BuildAstTmpl.createModelAttrNull(None, Some("name")),
    BuildAstTmpl.createModelAttrNull(None, Some("params")),
    BuildAstTmpl.createModelAttrNull(None, Some("attrs")),
  )))
}