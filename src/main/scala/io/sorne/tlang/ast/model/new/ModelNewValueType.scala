package io.sorne.tlang.ast.model.`new`

import io.sorne.tlang.ast.helper.Callable
import io.sorne.tlang.interpreter.Value

abstract class ModelNewValueType[T] extends Value[T] with Callable
