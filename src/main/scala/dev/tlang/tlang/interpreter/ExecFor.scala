package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.{ArrayValue, TLangLong}
import dev.tlang.tlang.ast.helper.{ForType, HelperFor, HelperStatement}
import dev.tlang.tlang.interpreter.context.{Context, MutableContext, Scope}
import tlang.core.{Long, Null, Value}

object ExecFor extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val forStatement = statement.asInstanceOf[HelperFor]
    forStatement.forType match {
      case ForType.IN => runForIn(forStatement, context) match {
        case Left(err) => Left(err)
        case Right(_) => Right(None)
      }
      case _ =>
        runFor(forStatement, context)
        Right(None)
    }
  }

  def runForIn(forStatement: HelperFor, context: Context): Either[ExecError, Unit] = {
    val array = ExecOperation.run(forStatement.array, context).toOption.get.get.head.asInstanceOf[ArrayValue].tbl.get
    val end = array.size
    val newLocalScope = Scope(local = true)
    val newScope = Scope()
    val newContext = MutableContext.toMutable(context).removeLocalScopes().addScope(newScope).addScope(newLocalScope).toContext()
    var error: Option[ExecError] = None
    var i = 0
    while (i < end && error.isEmpty) {
      ExecOperation.run(array(i).value, context) match {
        case Left(err) => error = Some(err)
        case Right(elem) =>
          newLocalScope.variables.update("_i", new TLangLong(Null.empty(), new Long(i)))
          newScope.variables.update(forStatement.variable, elem.get.head)
          ExecContent.run(forStatement.body, newContext)
      }
      i = i + 1
    }
    if (error.isDefined) Left(error.get)
    else Right(())
  }

  def runFor(forStatement: HelperFor, context: Context): Unit = {
    val start = ExecOperation.run(forStatement.start.get, context).toOption.get.get.head.asInstanceOf[TLangLong].getElement
    val end = ExecOperation.run(forStatement.array, context).toOption.get.get.head.asInstanceOf[TLangLong].getElement
    val realEnd = forStatement.forType match {
      case dev.tlang.tlang.ast.helper.ForType.TO => end.get()
      case dev.tlang.tlang.ast.helper.ForType.UNTIL => end.get() - 1
    }
    val newScope = Scope()
    val newContext = Context(context.scopes :+ newScope)
    for (i <- start.get() to realEnd) {
      newScope.variables.update(forStatement.variable, new TLangLong(Null.empty(), new Long(i)))
      ExecContent.run(forStatement.body, newContext)
    }
  }

}
