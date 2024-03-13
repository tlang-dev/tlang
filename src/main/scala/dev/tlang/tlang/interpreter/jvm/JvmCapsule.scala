package dev.tlang.tlang.interpreter.jvm

import dev.tlang.tlang.interpreter.context.State
import dev.tlang.tlang.interpreter.recipe.{Logger, Parameter}
import dev.tlang.tlang.interpreter.value.InterValue
import dev.tlang.tlang.interpreter.{Box, Program, Runner, Stack}
import tlang.core
import tlang.core.func.FuncRet
import tlang.core.{Entity, Null, Type}

import scala.collection.mutable

case class JvmCapsule(program: Program, logger: Logger, box: Box, staticBoxes: mutable.Map[String, Box], value: InterValue) extends Entity {
  override def getValue: JvmCapsule = this

  override def getType: Type = value.getType

  override def callFunc(name: core.String, args: core.Array): FuncRet = {
    val label = value.getAttrPath(name.toString)
    val start = program.getLabel(label)
    val boxes = mutable.Stack[Box]()
    boxes += box
    val stack = new Stack
    args.getRecords.reverse.foreach(value => stack.push(value))
    val state = State(program = program, boxes = boxes, staticBoxes = staticBoxes, stack = stack, logger = logger)
    new Runner().run(state, Parameter(start.section, start.instruction)) match {
      case Left(error) => FuncRet.error(error.message)
      case Right(value) => value match {
        case Some(value) => value.asInstanceOf[FuncRet]
        case None => FuncRet.error("No result returned")
      }
    }
  }

  override def getAttr(name: core.String): Null = {
    val label = value.getAttrPath(name.toString)
    val start = program.getLabel(label)
    val boxes = mutable.Stack[Box]()
    boxes += box
    val stack = new Stack
    val state = State(program = program, boxes = boxes, staticBoxes = staticBoxes, stack = stack, logger = logger)
    new Runner().run(state, Parameter(start.section, start.instruction)) match {
      case Left(_) => Null.empty()
      case Right(value) => value match {
        case Some(value) => Null.of(value)
        case None => Null.empty()
      }
    }
  }

  override def getAttr(index: core.Int): Null = {
    val label = value.getAttrPathByPos(index.get())
    val start = program.getLabel(label)
    val boxes = mutable.Stack[Box]()
    boxes += box
    val stack = new Stack
    val state = State(program = program, boxes = boxes, staticBoxes = staticBoxes, stack = stack, logger = logger)
    new Runner().run(state, Parameter(start.section, start.instruction)) match {
      case Left(_) => Null.empty()
      case Right(value) => value match {
        case Some(value) => Null.of(value)
        case None => Null.empty()
      }
    }
  }

  override def getModel: core.Null = Null.empty()
}
