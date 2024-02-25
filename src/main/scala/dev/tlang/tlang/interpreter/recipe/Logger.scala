package dev.tlang.tlang.interpreter.recipe

trait Logger {

  def debug(message: String): Unit

  def info(message: String): Unit

  def error(message: String): Unit

}
