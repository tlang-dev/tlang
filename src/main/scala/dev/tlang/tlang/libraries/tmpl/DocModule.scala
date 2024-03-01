package dev.tlang.tlang.libraries.tmpl

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.libraries.ModulePattern
import dev.tlang.tlang.tmpl.AstModel
import dev.tlang.tlang.tmpl.doc.ast.DocModel

object DocModule extends ModulePattern {

  override def getProject: String = "Tmpl"

  override def getName: String = "Doc"

  override def getFunctions: List[HelperFunc] = List()

  override def getModels: List[AstModel] = DocModel.getAll

  override def getMain: String = "Lang"

}
