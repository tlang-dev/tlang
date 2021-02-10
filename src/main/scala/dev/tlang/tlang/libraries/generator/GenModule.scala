package dev.tlang.tlang.libraries.generator

import io.sorne.tlang.ast.helper.HelperFunc
import io.sorne.tlang.libraries.ModulePattern

object GenModule extends ModulePattern{

  override def getProject: String = "Generator"

  override def getName: String = "Generator"

  override def getFunctions: List[HelperFunc] = List(
    Generator.generateFunc
  )
}
