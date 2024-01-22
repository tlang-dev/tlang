package dev.tlang.tlang.libraries.tmpl

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.libraries.ModulePattern
import dev.tlang.tlang.tmpl.data.ast.DataModel
import dev.tlang.tlang.tmpl.lang.ast.LangModel

object DataModule extends ModulePattern {

  override def getProject: String = "Tmpl"

  override def getName: String = "Data"

  override def getFunctions: List[HelperFunc] = List()

  override def getModels: List[ModelContent[_]] = DataModel.getAll

  override def getMain: String = "Lang"

}
