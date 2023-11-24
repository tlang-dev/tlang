package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.interpreter.Value

trait TmplNode[T] extends Value[T] with DeepCopy {

  def toEntity: EntityValue
}
