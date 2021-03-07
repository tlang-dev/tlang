package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.SimpleValueStatement
import dev.tlang.tlang.ast.model.set.ModelSetValueType

abstract class PrimitiveValue[TYPE] extends SimpleValueStatement[TYPE] with ModelSetValueType[TYPE] {

}
