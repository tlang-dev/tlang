package dev.tlang.tlang.interpreter.context

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.tmpl.TmplBlock
import dev.tlang.tlang.interpreter.Value
import io.sorne.tlang.ast.helper.HelperFunc
import io.sorne.tlang.ast.tmpl.TmplBlock
import io.sorne.tlang.interpreter.Value

import scala.collection.mutable

case class Scope(variables: mutable.Map[String, Value[_]] = mutable.Map(),
                 functions: mutable.Map[String, HelperFunc] = mutable.Map(),
                 templates: mutable.Map[String, TmplBlock] = mutable.Map())
