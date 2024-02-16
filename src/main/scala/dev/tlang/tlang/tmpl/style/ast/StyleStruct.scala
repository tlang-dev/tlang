package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.Null
import tlang.internal
import tlang.internal.{ContextContent, TmplID, TmplNode}

case class StyleStruct(context: Null[internal.ContextContent], name: Option[TmplID], params: Option[List[StyleAttribute[_]]], attrs: Option[List[StyleAttribute[_]]]) extends TmplNode[StyleStruct] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrNull(context, "name",
        if (name.isDefined) Null.of(name.get.toEntity) else Null.empty(),
        None
      ),
      BuildLang.createAttrNull(context, "params",
        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createAttrNull(context, "attrs",
        if (attrs.isDefined) Null.of(ArrayValue(context, Some(attrs.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      )
    ))
  )

  override def toModel: ModelSetEntity = StyleStruct.model

  override def getElement: StyleStruct = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): StyleStruct = StyleStruct(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[StyleAttribute[_]])) else None,
    if (attrs.isDefined) Some(attrs.get.map(_.deepCopy().asInstanceOf[StyleAttribute[_]])) else None)

  override def getContext: Null[ContextContent] = context

}

object StyleStruct {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, None, None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("attrs"), ModelSetType(Null.empty(), NullValue.name)),
  )))
}