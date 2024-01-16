package dev.tlang.tlang.interpreter.context

import dev.tlang.tlang.ast.common.call.CallRefFuncObject
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.set.ModelSetValueType
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.LangBlock

import scala.collection.mutable

case class Scope(name: String = "",
                 variables: mutable.Map[String, Value[_]] = mutable.Map(),
                 functions: mutable.Map[String, HelperFunc] = mutable.Map(),
                 templates: mutable.Map[String, LangBlock[_]] = mutable.Map(),
                 refFunctions: mutable.Map[String, CallRefFuncObject] = mutable.Map(),
                 models: mutable.Map[String, ModelSetValueType[_]] = mutable.Map(),
                 local: Boolean = false)
