package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{HelperCallObject, HelperStatement}

object ExecCallObject extends Executor {

  override def run(statement: HelperStatement, context: Context): Option[Value[_]] = {
    val arg1 = statement.asInstanceOf[HelperCallObject]
    if (!arg1.statement.contains('.')) findObj(arg1.statement, context)
    else {
      val split = arg1.statement.split('.')
      findObj(split.head, context) match {
        case Some(value) =>
        case None => None
      }
    }
    None
  }

  private def findObj(elem: String, context: Context): Option[Value[_]] = {
    context.variables.get(elem)
  }

}
