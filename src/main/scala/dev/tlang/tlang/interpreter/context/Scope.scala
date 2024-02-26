package dev.tlang.tlang.interpreter.context

import dev.tlang.tlang.ast.common.call.CallRefFuncObject
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.set.ModelSetValueType
import dev.tlang.tlang.tmpl.AnyTmplInterpretedBlock
import tlang.core.Value

import scala.collection.mutable

case class Scope(name: String = "",
                 variables: mutable.Map[String, Value] = mutable.Map(),
                 functions: mutable.Map[String, HelperFunc] = mutable.Map(),
                 templates: mutable.Map[String, AnyTmplInterpretedBlock[_]] = mutable.Map(),
                 refFunctions: mutable.Map[String, CallRefFuncObject] = mutable.Map(),
                 models: mutable.Map[String, ModelSetValueType[_]] = mutable.Map(),
                 local: Boolean = false)
