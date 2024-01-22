package dev.tlang.tlang.libraries.tmpl

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.libraries.ModulePattern
import dev.tlang.tlang.tmpl.style.ast.StyleModel

object StyleModule extends ModulePattern {

  override def getProject: String = "Tmpl"

  override def getName: String = "Style"

  override def getFunctions: List[HelperFunc] = List()

  override def getModels: List[ModelContent[_]] = StyleModel.getAll

  override def getMain: String = "Lang"

}
