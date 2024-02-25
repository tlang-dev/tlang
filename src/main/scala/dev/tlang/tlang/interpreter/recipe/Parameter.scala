package dev.tlang.tlang.interpreter.recipe

case class Parameter(
                      sectionStart: Int,
                      instrStart: Int,
                      logger: Logger = new DefaultLogger)
