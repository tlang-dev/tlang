package dev.tlang.tlang.libraries.tmpl

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.libraries.ModulePattern
import dev.tlang.tlang.tmpl.cmd.ast.CmdModel
import dev.tlang.tlang.tmpl.lang.ast.LangModel

object CmdModule extends ModulePattern {

  override def getProject: String = "Tmpl"

  override def getName: String = "Cmd"

  override def getFunctions: List[HelperFunc] = List()

  override def getModels: List[ModelContent[_]] = CmdModel.getAll

  override def getMain: String = "Lang"

}
