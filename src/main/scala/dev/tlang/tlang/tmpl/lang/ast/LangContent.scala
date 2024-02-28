package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.{AstModel, AstTmplNode}
import tlang.core.Type

trait LangContent[TYPE] extends AstTmplNode


object LangContent {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
  )))
}