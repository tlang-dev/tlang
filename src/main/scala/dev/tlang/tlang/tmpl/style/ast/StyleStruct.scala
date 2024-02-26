package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import tlang.core.{Null, Type}
import tlang.internal.{TmplID, TmplNode}

case class StyleStruct(context: Null, name: Option[TmplID], params: Option[List[StyleAttribute[_]]], attrs: Option[List[StyleAttribute[_]]]) extends TmplNode[StyleStruct] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, StyleStruct.modelName)),
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

  override def getContext: Null = context

}

object StyleStruct {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, None, None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("attrs"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}