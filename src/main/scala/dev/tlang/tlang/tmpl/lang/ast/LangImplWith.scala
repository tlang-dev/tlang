package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent

case class LangImplWith(context: Option[ContextContent], var props: Option[LangProp] = None, var types: List[LangType]) extends DeepCopy {
  override def deepCopy(): LangImplWith = LangImplWith(context,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    types.map(_.deepCopy()))
}

object LangImplWith {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}
