package dev.tlang.tlang.libraries.std.str_builder

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.libraries.ModulePattern

object StdModule extends ModulePattern {
  override def getName: String = "StrBuilder"

  override def getProject: String = "Std"

  override def getFunctions: List[HelperFunc] = List()

  override def getModels: List[ModelContent[_]] = List(Std.strBuilder)
}
