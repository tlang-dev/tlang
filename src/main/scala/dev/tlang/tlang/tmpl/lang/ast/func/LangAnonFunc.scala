package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.lang.ast.{LangExprContent, LangExpression, LangModel}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangAnonFunc(context: Option[ContextContent], var curries: Option[List[LangFuncParam]], var content: LangExprContent[_]) extends LangExpression[LangAnonFunc] {
  override def getElement: LangAnonFunc = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): LangAnonFunc = LangAnonFunc(context,
    if (curries.isDefined) Some(curries.get.map(curry => curry.deepCopy())) else None,
    content.deepCopy().asInstanceOf[LangExprContent[_]])

  override def compareTo(value: Value[LangAnonFunc]): Int = 0

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAnonFunc.name)),
    Some(List(
      BuildLang.createAttrNull(context, "curries",
        if (curries.isDefined) Some(ArrayValue(context, Some(curries.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createAttrEntity(context, "content", content.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangAnonFunc.model
}

object LangAnonFunc {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("curries"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("content"), ModelSetType(None, LangExprContent.name)),
  )))
}
