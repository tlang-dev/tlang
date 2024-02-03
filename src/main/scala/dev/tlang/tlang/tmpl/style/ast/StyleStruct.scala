package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class StyleStruct(context: Option[ContextContent], name: Option[TmplID], params: Option[List[StyleAttribute[_]]], attrs: Option[List[StyleAttribute[_]]]) extends TmplNode[StyleStruct] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrNull(context, "name",
        if (name.isDefined) Some(name.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrNull(context, "params",
        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createAttrNull(context, "attrs",
        if (attrs.isDefined) Some(ArrayValue(context, Some(attrs.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      )
    ))
  )

  override def toModel: ModelSetEntity = StyleStruct.model

  override def compareTo(value: Value[StyleStruct]): Int = 0

  override def getElement: StyleStruct = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): StyleStruct = StyleStruct(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[StyleAttribute[_]])) else None,
    if (attrs.isDefined) Some(attrs.get.map(_.deepCopy().asInstanceOf[StyleAttribute[_]])) else None)

  override def getContext: Option[ContextContent] = context
}

object StyleStruct {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, None, None, Some(List(
    ModelSetAttribute(None, Some("name"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("params"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("attrs"), ModelSetType(None, NullValue.name)),
  )))
}