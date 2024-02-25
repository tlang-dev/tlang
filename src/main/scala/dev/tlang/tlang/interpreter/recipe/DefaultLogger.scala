package dev.tlang.tlang.interpreter.recipe

class DefaultLogger extends Logger {

  override def debug(message: String): Unit = println(s"[Debug] $message")

  override def info(message: String): Unit = println(s"[Info] $message")

  override def error(message: String): Unit = println(s"[Error] $message")

}
