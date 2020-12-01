package io.sorne.tlang.ast.common.value

import io.sorne.tlang.ast.model.set.ModelSetRefValue
import io.sorne.tlang.interpreter.Value

trait ComplexValueType[T] extends Value[T] with ModelSetRefValue
