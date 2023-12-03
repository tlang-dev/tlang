package dev.tlang.tlang.libraries.std.entity

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.libraries.ModulePattern

object StdEntityModule extends ModulePattern {
  override def getName: String = "Entity"

  override def getProject: String = "Std"

  override def getFunctions: List[HelperFunc] = List(
    StdEntity.existsFunc
  )

  override def getModels: List[ModelContent[_]] = List()

}
