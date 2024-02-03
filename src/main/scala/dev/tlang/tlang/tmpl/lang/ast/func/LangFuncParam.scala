package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, NullValue, TLangString}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode
import dev.tlang.tlang.tmpl.lang.ast.{LangModel, LangParam}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangFuncParam(context: Option[ContextContent], params: Option[List[LangParam]], var `type`: String) extends TmplNode[LangFuncParam] {
  override def compareTo(value: Value[LangFuncParam]): Int = 0

  override def getElement: LangFuncParam = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): LangFuncParam = LangFuncParam(context,
    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None,
    `type`
  )

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangFuncParam.name)),
    Some(List(
      BuildLang.createAttrNull(context, "params",
        if (params.isDefined) Some(ArrayValue(context, Some(params.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createAttrStr(context, "tType", `type`),
    ))
  )

  override def toModel: ModelSetEntity = LangFuncParam.model
}

object LangFuncParam {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("params"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("tType"), ModelSetType(None, TLangString.getType)),
  )))
}
