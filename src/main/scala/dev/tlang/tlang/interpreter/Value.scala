package dev.tlang.tlang.interpreter

trait Value[T] {

  def getValue: T

  def getType: String

  def compareTo(value: Value[T]): Int

}