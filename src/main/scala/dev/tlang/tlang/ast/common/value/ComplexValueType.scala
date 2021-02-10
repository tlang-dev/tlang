package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.ast.model.set.ModelSetRefValue
import dev.tlang.tlang.interpreter.Value

trait ComplexValueType[T] extends Value[T] with ModelSetRefValue
