package io.sorne.tlang.interpreter.context

import io.sorne.tlang.ast.helper.HelperFunc
import io.sorne.tlang.interpreter.Value

object ContextUtils {

  def findVar(context: Context, name: String): Option[Value[_]] = {
    var i = 0
    var variable: Option[Value[_]] = None
    while (variable.isEmpty && i < context.scopes.length) {
      context.scopes(i).variables.get(name).foreach(value => variable = Some(value))
      i += 1
    }
    variable
  }

  def findFunc(context: Context, name: String): Option[HelperFunc] = {
    var i = 0
    var func: Option[HelperFunc] = None
    while (func.isEmpty && i < context.scopes.length) {
      context.scopes(i).functions.get(name).foreach(value => func = Some(value))
      i += 1
    }
    func
  }

}