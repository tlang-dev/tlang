package dev.tlang.tlang.interpreter

import tlang.core.Value

import scala.collection.mutable

class Stack {

  private val stack = new mutable.Stack[Value]()

  def push(value: Value): Unit = stack.push(value)

  def pop(): Value = stack.pop()

  def load(): Value = stack(0)

  def loadAt(index: Int): Value = stack(index)

  def deleteAt(index: Int): Value = stack.remove(index)

  def delete(): Unit = stack.pop()

}
