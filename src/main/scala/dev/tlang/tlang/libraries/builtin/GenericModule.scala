package dev.tlang.tlang.libraries.builtin

import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.libraries.ModulePattern

import scala.collection.mutable.ListBuffer

case class GenericModule(
                          project: String,
                          name: String,
                          functions: ListBuffer[HelperFunc] = ListBuffer.empty,
                          models: ListBuffer[ModelContent[_]] = ListBuffer.empty)
  extends ModulePattern {

  override def getProject: String = project

  override def getName: String = name

  override def getFunctions: List[HelperFunc] = functions.toList

  override def getModels: List[ModelContent[_]] = models.toList

}
