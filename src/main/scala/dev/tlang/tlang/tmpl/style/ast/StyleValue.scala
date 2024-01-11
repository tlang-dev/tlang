package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.TmplNode

case class StyleValue()extends TmplNode[StyleValue] {
  override def toEntity: EntityValue = ???

  override def toModel: ModelSetEntity = ???

  override def compareTo(value: Value[StyleValue]): Int = ???

  override def getElement: StyleValue = ???

  override def getType: String = ???

  override def deepCopy(): Any = ???

  override def getContext: Option[ContextContent] = ???
}

object StyleValue {

}
