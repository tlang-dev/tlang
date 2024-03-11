package dev.tlang.tlang.interpreter.jvm

import dev.tlang.tlang.interpreter.context.State
import dev.tlang.tlang.interpreter.recipe.Parameter
import dev.tlang.tlang.interpreter.value.InterValue
import dev.tlang.tlang.interpreter.{Box, Program, Runner, Stack}
import tlang.core
import tlang.core.func.FuncRet
import tlang.core.{Type, Value}

import scala.collection.mutable

case class JvmCapsule(program: Program, box: Box, staticBoxes: mutable.Map[String, Box], value: InterValue) extends Value {
  override def getValue: JvmCapsule = this

  override def getType: Type = value.getType

  override def callFunc(name: core.String, args: core.Array): FuncRet = {
    val label = value.getAttrPath(name.toString)
    val start = program.getLabel(label)
    val boxes = mutable.Stack[Box]()
    boxes += box
    val stack = new Stack
    args.getRecords.reverse.foreach(value => stack.push(value))
    val state = State(program = program, boxes = boxes, staticBoxes = staticBoxes, stack = stack)
    new Runner().run(state, Parameter(start.section, start.instruction))
    FuncRet.of(stack.pop())
  }

  override def getAttr(name: core.String): Value = {
    val label = value.getAttrPath(name.toString)
    val start = program.getLabel(label)
    val boxes = mutable.Stack[Box]()
    boxes += box
    val stack = new Stack
    val state = State(program = program, boxes = boxes, staticBoxes = staticBoxes, stack = stack)
    new Runner().run(state, Parameter(start.section, start.instruction))
    FuncRet.of(stack.pop())
  }

  override def getAttr(index: core.Int): Value = {
    val label = value.getAttrPathByPos(index.get())
    val start = program.getLabel(label)
    val boxes = mutable.Stack[Box]()
    boxes += box
    val stack = new Stack
    val state = State(program = program, boxes = boxes, staticBoxes = staticBoxes, stack = stack)
    new Runner().run(state, Parameter(start.section, start.instruction))
    FuncRet.of(stack.pop())
  }
}
