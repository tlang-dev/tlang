package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{HelperCallObject, HelperFor, HelperFunc, HelperIf, HelperStatement}

object ExecStatement extends Executor {
  override def run(statement: HelperStatement, context: Context): Option[Value[_]] = {
    statement match {
      case HelperIf(_, _, _) => ExecIf.run(statement, context)
      case HelperCallObject() => ExecCallObject.run(statement, context)
      case HelperFor() => ExecFor.run(statement, context)
      case HelperFunc() => ExecFunc.run(statement, context)
    }
    None
  }
}
