package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.interpreter.value.InterJVM

object BuildCallJVM {

  def applyJVM(context: BuilderContext, value: InterJVM, callObject: CallObject, callIndex: Int): Unit = {
    //    context.section.addInstruction(Get(value.pos))
    if (callIndex < callObject.statements.size - 1) {
    }
  }
}
