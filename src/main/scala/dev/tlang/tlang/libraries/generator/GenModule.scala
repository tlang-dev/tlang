package dev.tlang.tlang.libraries.generator

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.libraries.ModulePattern
import dev.tlang.tlang.tmpl.AstModel

object GenModule extends ModulePattern{

  override def getProject: String = "Generator"

  override def getName: String = "Generator"

  override def getFunctions: List[HelperFunc] = List(
//    Generator.generateFunc
  )

  override def getModels: List[AstModel] = List()
}
