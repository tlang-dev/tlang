package dev.tlang.tlang.interpreter

import tlang.core.Value

import scala.collection.mutable

case class Box(stack: mutable.Stack[Value[_]] = new mutable.Stack()) {

  def getAt(index: Int): Value[_] = stack(index)

  def get(): Value[_] = stack(0)

  def getRemoveAt(index: Int): Value[_] = stack.remove(index)

  def pop(): Value[_] = stack.pop()

  def setAt(index: Int, value: Value[_]): Unit = stack.insert(index, value)

  def set(value: Value[_]): Unit = stack.prepend(value)

  def unsetAt(index: Int): Unit = stack.remove(index)

  def unset(): Unit = stack.remove(0)

  def clean(): Unit = stack.clear()
}
