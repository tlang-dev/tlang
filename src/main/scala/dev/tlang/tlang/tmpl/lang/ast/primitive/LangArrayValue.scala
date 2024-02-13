package dev.tlang.tlang.tmpl.lang.ast.primitive

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangType}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class LangArrayValue(context: Null[ContextContent], var `type`: Option[LangType] = None, var params: Option[List[TmplNode[_]]]) extends LangPrimitiveValue[LangArrayValue] {
  override def deepCopy(): LangArrayValue = LangArrayValue(context,
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None)


  override def compareTo(value: Value[LangArrayValue]): Int = 0

  override def getElement: LangArrayValue = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangArrayValue.name)),
    Some(List(
      BuildLang.createAttrNull(context, "tType",
        if (`type`.isDefined) Some(`type`.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrNull(context, "params",
        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      )
    ))
  )

  override def toModel: ModelSetEntity = LangArrayValue.model
}

object LangArrayValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("tType"), ModelSetType(Null.empty(), NullValue.name)),
    ModelSetAttribute(Null.empty(), Some("params"), ModelSetType(Null.empty(), NullValue.name)),
  )))
}
