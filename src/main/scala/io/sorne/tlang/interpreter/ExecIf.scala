package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{HelperIf, HelperStatement}
import io.sorne.tlang.interpreter.`type`.Bool

object ExecIf extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[Value[_]]] = {
    val ifStatement = statement.asInstanceOf[HelperIf]
    ExecStatement.run(ifStatement.statement1, context) match {
      case Left(value) => Left(value)
      case Right(value) => value match {
        case Some(valType) => valType match {
          case bool: Bool => if (bool.getValue) execIfTrue(ifStatement, context) else execIfFalse(ifStatement, context)
        }
        case None => Left(NotACondition())
      }
    }
  }

  private def execIfTrue(helperIf: HelperIf, context: Context): Either[ExecError, Option[Value[_]]] = {
    if (helperIf.ifTrue.isDefined) {
      if (helperIf.ifTrue.get.content.isDefined) ExecStatement.run(helperIf.ifTrue.get.content.get.last, context)
      else Right(None)
    } else Right(None)
  }

  private def execIfFalse(helperIf: HelperIf, context: Context): Either[ExecError, Option[Value[_]]] = {
    if (helperIf.ifFalse.isDefined) {
      if (helperIf.ifFalse.get.content.isDefined) ExecStatement.run(helperIf.ifFalse.get.content.get.last, context)
      else Right(None)
    } else Right(None)
  }

}
