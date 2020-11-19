package io.sorne.tlang.interpreter

import io.sorne.tlang.ast.helper.{HelperNewMultiValue, HelperStatement}
import io.sorne.tlang.ast.model.let.ModelLetMultiValue
import io.sorne.tlang.interpreter.context.Context

import scala.collection.mutable.ListBuffer

object ExecNewMultiValue extends Executor {

  override def run(statement: HelperStatement, context: Context): Either[ExecError, Option[List[Value[_]]]] = {
    val multiStatement = statement.asInstanceOf[HelperNewMultiValue]
    val values = ListBuffer.empty[Value[_]]
    var error: Option[ExecError] = None
    var i = 0
    while (error.isEmpty && i < multiStatement.values.size) {
      multiStatement.values(i) match {
        case Left(obj) => ExecCallObject.run(obj, context) match {
          case Left(err) => error = Some(err)
          case Right(value) => convertValues(value) match {
            case Left(err) => error = Some(err)
            case Right(value) => values.addOne(value)
          }
        }
        case Right(value) => values.addOne(value)
      }
      i += 1
    }
    if (error.isDefined) Left(error.get)
    else Right(Some(values.toList))
  }

  def convertValues(value: Option[List[Value[_]]]): Either[ExecError, Value[_]] = {
    value match {
      case Some(v) => if (v.isEmpty) Left(NoValue("No value found"))
      else if (v.size == 1) Right(v.head)
      else Right(ModelLetMultiValue(v))
      case None => Left(NoValue("No value found"))
    }
  }

}
