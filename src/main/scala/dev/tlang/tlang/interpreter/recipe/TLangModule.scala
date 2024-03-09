package dev.tlang.tlang.interpreter.recipe

import dev.tlang.tlang.interpreter.value.InterJVM

case class TLangModule(name: String, project: String, classes: Map[String, InterJVM]) {

}
