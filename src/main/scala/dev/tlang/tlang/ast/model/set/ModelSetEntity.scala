package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.ast.common.ValueType
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.interpreter.context.Scope
import tlang.core
import tlang.core.Type

case class ModelSetEntity(context: core.Null, name: Type, ext: Option[ValueType], params: Option[List[ModelSetAttribute]], attrs: Option[List[ModelSetAttribute]],
                          scope: Scope = Scope()) extends ModelContent[ModelSetEntity] with ModelSetValueType[ModelSetEntity] {


}
