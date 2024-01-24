package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangNode}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangCallFuncParam(context: Option[ContextContent], var params: Option[List[LangNode[_]]]) extends LangNode[LangCallFuncParam] {
  override def compareTo(value: Value[LangCallFuncParam]): Int = 0

  override def getElement: LangCallFuncParam = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): LangCallFuncParam = LangCallFuncParam(
    context,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[LangNode[_]])) else None,
  )

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallFuncParam.name)),
    Some(List(
      BuildLang.createAttrNull(context, "params",
        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      )
    ))
  )

  override def toModel: ModelSetEntity = LangCallFuncParam.model
}

object LangCallFuncParam {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("params"), ModelSetType(None, NullValue.name)),
  )))
}
