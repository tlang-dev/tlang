package dev.tlang.tlang.interpreter

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.common.value.MultiValue
import dev.tlang.tlang.ast.helper.HelperStatement
import dev.tlang.tlang.interpreter.context.Context
import dev.tlang.tlang.tmpl.AstContext
import tlang.core.{Null, Value}

import scala.collection.mutable.ListBuffer

object ExecMultiValue extends Executor {

  override def run(statement: HelperStatement, context: AstContext): Either[ExecError, Option[List[Value]]] = {
    val multiStatement = statement.asInstanceOf[MultiValue]
    val values = ListBuffer.empty[Value]
    var error: Option[ExecError] = None
    var i = 0
    while (error.isEmpty && i < multiStatement.values.size) {
      multiStatement.values(i) match {
        case value: ComplexValueStatement[_] => ExecComplexValue.run(value, context) match {
          case Left(err) => error = Some(err)
          case Right(value) => convertValues(value) match {
            case Left(err) => error = Some(err)
            case Right(value) => values.addOne(value)
          }
        }
        case value: Value => values.addOne(value)
      }
      i += 1
    }
    if (error.isDefined) Left(error.get)
    else Right(Some(values.toList))
  }

  private def convertValues(value: Option[List[Value]]): Either[ExecError, Value] = {
    value match {
      case Some(v) => if (v.isEmpty) Left(NoValue("No value found", None))
      else if (v.size == 1) Right(v.head)
      else Right(null)
      //else Right(MultiValue(None, v).asInstanceOf[Value])
      case None => Left(NoValue("No value found", None))
    }
  }

}
