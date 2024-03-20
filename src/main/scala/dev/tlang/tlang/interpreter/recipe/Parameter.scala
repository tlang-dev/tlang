package dev.tlang.tlang.interpreter.recipe

case class Parameter(
                      sectionStart: Int = 0,
                      instrStart: Int = 0,
                      startLabel: Option[String] = None,
                      logger: Logger = new DefaultLogger)
