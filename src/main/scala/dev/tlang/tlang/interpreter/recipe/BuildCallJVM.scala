package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.interpreter.instruction.CallJVM
import dev.tlang.tlang.interpreter.recipe.BuildProgram.buildSetAttribute
import dev.tlang.tlang.interpreter.value.InterJVM

object BuildCallJVM {

  def applyJVM(context: BuilderContext, value: InterJVM, callObject: CallObject, callIndex: Int): Unit = {
    //    context.section.addInstruction(Get(value.pos))
    callObject.statements(callIndex) match {
      case array: CallArrayObject => ???
      case func: CallFuncObject => applyFunc(context, func, value)
      case CallRefFuncObject(context, name, currying, func, scope) => ???
      case CallVarObject(context, name) => ???
      case _ => ???
    }
  }

  private def applyFunc(context: BuilderContext, callFunc: CallFuncObject, value: InterJVM): Unit = {
    var totParam = 0
    callFunc.currying.foreach(_.foreach(_.params.foreach(params => params.foreach(attr => {
      buildSetAttribute(context, attr)
      totParam += 1
    }))))
    context.section.addInstruction(CallJVM(value, callFunc.name.get, totParam))
  }
}
