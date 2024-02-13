package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import dev.tlang.tlang.tmpl.{DeepCopy, TmplNode}
import tlang.core.{Null, Value}
import tlang.internal.{AstContext, ContextContent, TmplID}

case class LangAnnotationParam(context: Null[ContextContent], var name: Option[TmplID], var value: LangValueType[_]) extends DeepCopy with TmplNode[LangAnnotationParam] with AstContext {
  override def deepCopy(): LangAnnotationParam =
    LangAnnotationParam(context,
      if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
      value.deepCopy().asInstanceOf[LangValueType[_]])

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[LangAnnotationParam]): Int = 0

  override def getElement: LangAnnotationParam = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAnnotationParam.name)),
    Some(List(
      BuildLang.createAttrNull(context, "name",
        if (name.isDefined) Null.of(name.get.toEntity) else Null.empty(),
        None
      ),
    ))
  )

  override def toModel: ModelSetEntity = LangAnnotationParam.model
}

object LangAnnotationParam {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), NullValue.name)),
  )))
}
