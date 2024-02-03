package dev.tlang.tlang.tmpl

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.interpreter.Value

trait TmplNode[T] extends Value[T] with DeepCopy {

  def toEntity: EntityValue

  def toModel: ModelSetEntity
}

object TmplNode {
  val name: String = this.getClass.getSimpleName.replace("$", "")
}
