package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{ArrayValue, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangImplFor(context: Option[ContextContent], var props: Option[LangProp] = None, var types: List[LangType]) extends LangNode[LangImplFor] with AstContext {
  override def deepCopy(): LangImplFor = LangImplFor(context,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    types.map(_.deepCopy()))

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangImplFor.name)),
    Some(List(
      BuildLang.createAttrNull(context, "props",
        if (props.isDefined) Some(props.get.toEntity) else None,
        None
      ),
      BuildLang.createArray(context, "types", types.map(_.toEntity))
    ))
  )

  override def toModel: ModelSetEntity = LangImplFor.model

  override def compareTo(value: Value[LangImplFor]): Int = 0

  override def getElement: LangImplFor = this

  override def getType: String = getClass.getSimpleName
}

object LangImplFor {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("props"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("types"), ModelSetType(None, ArrayValue.getType)),
  )))
}