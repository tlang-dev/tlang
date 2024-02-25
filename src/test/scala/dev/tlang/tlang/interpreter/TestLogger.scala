package dev.tlang.tlang.interpreter

import dev.tlang.tlang.interpreter.recipe.Logger

import scala.collection.mutable.ListBuffer

class TestLogger extends Logger {

  private val logs = new ListBuffer[String].empty

  override def debug(message: String): Unit = {
    logs += message
  }

  override def info(message: String): Unit = {
    logs += message
  }

  override def error(message: String): Unit = {
    logs += message
  }

  def getLogs: List[String] = logs.toList
}
