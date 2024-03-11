package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.interpreter.context.JumpIndex
import dev.tlang.tlang.interpreter.instruction.{Back, GotoLabel}
import dev.tlang.tlang.interpreter.value.InterEntity

object BuildCallEntity {

  def applyEntity(context: BuilderContext, value: InterEntity, callObject: CallObject, callIndex: Int): Unit = {
    callObject.statements(callIndex -1) match {
      case CallArrayObject(context, name, position) => ???
      case callFunc: CallFuncObject => applyFunc(context, callFunc, value)
      case CallRefFuncObject(context, name, currying, func, scope) => ???
      case callVar: CallVarObject => applyVar(context, callVar, value)
      case _ => ???
    }
  }

  private def applyVar(context: BuilderContext, callVar: CallVarObject, value: InterEntity): Unit = {
    val label = value.`type`.getType.toString
    context.section.addInstruction(GotoLabel(label))
    context.section.addInstruction(Back(JumpIndex(context.sectionPos, context.instrPos + 2)))
  }

  private def applyFunc(context: BuilderContext, callFunc: CallFuncObject, value: InterEntity): Unit = {

  }

}
