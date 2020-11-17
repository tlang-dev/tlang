package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{ForType, HelperFor, HelperStatement}
import io.sorne.tlang.ast.model.`new`.ModelNewArrayValue
import io.sorne.tlang.interpreter.`type`.TLangInt
import io.sorne.tlang.interpreter.context.{Context, Scope}

object ExecFor extends Executor {
  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val forStatement = statement.asInstanceOf[HelperFor]
    forStatement.forType match {
      case ForType.IN => runForIn(forStatement, context)
      case _ => runFor(forStatement, context)
    }
    Right(None)
  }

  def runForIn(forStatement: HelperFor, context: Context): Unit = {
    val array = ExecCallObject.run(forStatement.array, context).toOption.get.get.head.asInstanceOf[ModelNewArrayValue].tbl.get
    val end = array.size
    val newScope = Scope()
    val newContext = Context(context.scopes.appended(newScope))
    for (i <- 0 until end) {
      val elem = array(i).value
      newScope.variables.update("_i", new TLangInt(i))
      newScope.variables.update(forStatement.variable, elem)
      ExecContent.run(forStatement.body, newContext)
    }
  }

  def runFor(forStatement: HelperFor, context: Context): Unit = {
    val start = ExecCallObject.run(forStatement.start.get, context).toOption.get.get.head.asInstanceOf[TLangInt].getValue
    val end = ExecCallObject.run(forStatement.array, context).toOption.get.get.head.asInstanceOf[TLangInt].getValue
    val realEnd = forStatement.forType match {
      case io.sorne.tlang.ast.helper.ForType.TO => end
      case io.sorne.tlang.ast.helper.ForType.UNTIL => end - 1
    }
    val newScope = Scope()
    val newContext = Context(context.scopes.appended(newScope))
    for (i <- start to realEnd) {
      newScope.variables.update(forStatement.variable, new TLangInt(i))
      ExecContent.run(forStatement.body, newContext)
    }
  }

}
