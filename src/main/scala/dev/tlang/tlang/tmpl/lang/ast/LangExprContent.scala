package dev.tlang.tlang.tmpl.lang.ast

trait LangExprContent[TYPE] extends LangContent[TYPE]

object LangExprContent {
  val name: String = this.getClass.getSimpleName.replace("$", "")
}
