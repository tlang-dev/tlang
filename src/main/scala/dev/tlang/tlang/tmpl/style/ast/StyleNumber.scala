package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.TmplNode

case class StyleNumber() extends TmplNode[StyleNumber] {
  override def toEntity: EntityValue = ???

  override def toModel: ModelSetEntity = ???

  override def compareTo(value: Value[StyleNumber]): Int = ???

  override def getElement: StyleNumber = ???

  override def getType: String = ???

  override def deepCopy(): Any = ???

  override def getContext: Option[ContextContent] = ???
}

object StyleNumber {

}