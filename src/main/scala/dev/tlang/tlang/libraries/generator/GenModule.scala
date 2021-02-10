package dev.tlang.tlang.libraries.generator

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.libraries.ModulePattern

object GenModule extends ModulePattern{

  override def getProject: String = "Generator"

  override def getName: String = "Generator"

  override def getFunctions: List[HelperFunc] = List(
    Generator.generateFunc
  )
}
