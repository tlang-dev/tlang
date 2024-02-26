package dev.tlang.tlang.interpreter

import tlang.core.Value

import scala.collection.mutable

case class Box(stack: mutable.Stack[Value] = new mutable.Stack()) {

  def getAt(index: Int): Value = stack(index)

  def get(): Value = stack(0)

  def getRemoveAt(index: Int): Value = stack.remove(index)

  def pop(): Value = stack.pop()

  def setAt(index: Int, value: Value): Unit = stack.insert(index, value)

  def replaceAt(index: Int, value: Value): Unit = stack(index) = value

  def set(value: Value): Unit = stack.prepend(value)

  def unsetAt(index: Int): Unit = stack.remove(index)

  def unset(): Unit = stack.remove(0)

  def clean(): Unit = stack.clear()
}
