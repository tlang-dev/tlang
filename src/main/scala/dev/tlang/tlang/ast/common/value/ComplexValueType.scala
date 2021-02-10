package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.interpreter.Value
import io.sorne.tlang.ast.model.set.ModelSetRefValue
import io.sorne.tlang.interpreter.Value

trait ComplexValueType[T] extends Value[T] with ModelSetRefValue
