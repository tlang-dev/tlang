package dev.tlang.tlang.interpreter

import tlang.core.Value

import scala.collection.mutable

class Stack {

  private val stack = new mutable.Stack[Value[_]]()

  def push(value: Value[_]): Unit = stack.push(value)

  def pop(): Value[_] = stack.pop()

  def load(): Value[_] = stack(0)

  def loadAt(index: Int): Value[_] = stack(index)

  def deleteAt(index: Int): Value[_] = stack.remove(index)

  def delete(): Unit = stack.pop()

}
