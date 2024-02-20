package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.cmd.ast.CmdCallFuncArgs.{getClass, name}
import tlang.core.Type

trait LangExprContent[TYPE] extends LangContent[TYPE]

object LangExprContent {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)
}
