package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.TmplNode

case class StyleBool() extends TmplNode[StyleBool] {
  override def toEntity: EntityValue = ???

  override def toModel: ModelSetEntity = ???

  override def compareTo(value: Value[StyleBool]): Int = ???

  override def getElement: StyleBool = ???

  override def getType: String = ???

  override def deepCopy(): Any = ???

  override def getContext: Option[ContextContent] = ???
}

object StyleBool {

}