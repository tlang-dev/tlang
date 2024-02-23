package dev.tlang.tlang.libraries.tmpl

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.libraries.ModulePattern
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core

object LangModule extends ModulePattern {

  override def getProject: String = "Tmpl"

  override def getName: String = "Lang"

  override def getFunctions: List[HelperFunc] = List.empty

  override def getModels: List[ModelContent[_]] = LangModel.getAll

  override def getMain: String = "Lang"

}
