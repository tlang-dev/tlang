package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.value.TLangBool
import dev.tlang.tlang.ast.helper.{HelperIf, HelperStatement}
import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Value

object ExecIf extends Executor {

  override def run(statement: HelperStatement, context: AstContext): Either[ExecError, Option[List[Value]]] = {
    val ifStatement = statement.asInstanceOf[HelperIf]
    ExecOperation.run(ifStatement.condition, context) match {
      case Left(value) => Left(value)
      case Right(value) => value match {
        case Some(valType) => if (valType.size == 1) valType.head match {
          case bool: TLangBool => if (bool.getElement.getValue) execIfTrue(ifStatement, context) else execIfFalse(ifStatement, context)
        } else Left(WrongNumberOfArguments("expected 1 got " + valType.size, None))
        case None => Left(NotACondition(None))
      }
    }
  }

  private def execIfTrue(helperIf: HelperIf, context: AstContext): Either[ExecError, Option[List[Value]]] = {
    if (helperIf.ifTrue.isDefined) {
      if (helperIf.ifTrue.get.content.isDefined) ExecContent.run(helperIf.ifTrue.get, context)
      else Right(None)
    } else Right(None)
  }

  private def execIfFalse(helperIf: HelperIf, context: AstContext): Either[ExecError, Option[List[Value]]] = {
    if (helperIf.ifFalse.isDefined) {
      if (helperIf.ifFalse.get.content.isDefined) ExecContent.run(helperIf.ifFalse.get, context)
      else Right(None)
    } else Right(None)
  }

}
