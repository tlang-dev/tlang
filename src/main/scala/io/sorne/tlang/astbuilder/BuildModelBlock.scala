package io.sorne.tlang.astbuilder

import io.sorne.tlang.TLangParser._
import io.sorne.tlang.ast.model._

import scala.jdk.CollectionConverters._

object BuildModelBlock {

  def build(model: ModelBlockContext): ModelBlock = {
    val content = model.modelContents.asScala.map {
      case content@_ if content.modelNewEntity() != null => buildNewEntity(content.modelNewEntity())
      case content@_ if content.modelSetEntity() != null => buildSetEntity(content.modelSetEntity())
    }.toList
    ModelBlock(if (content.nonEmpty) Some(content) else None)
  }

  def buildNewEntity(newEntity: ModelNewEntityContext): ModelNewEntity = {
    ModelNewEntity()
  }

  def buildSetEntity(setEntity: ModelSetEntityContext): ModelSetEntity = {
    val content = setEntity.params.asScala.map(attr => attr.modelSetValueType() match {
      case content@_ if content.modelSetType() != null => ModelSetAttribute(Option(attr.attr.getText), buildType(content.modelSetType()))
      case content@_ if content.modelSetFuncDef() != null => ModelSetAttribute(Option(attr.attr.getText), buildFuncDef(content.modelSetFuncDef()))
      case content@_ if content.modelSetRef() != null => ModelSetAttribute(Option(attr.attr.getText), buildRef(content.modelSetRef()))
    }).toList
    ModelSetEntity(setEntity.name.getText, if (content.nonEmpty) Some(content) else None)
  }


  def buildType(setType: ModelSetTypeContext): ModelSetType = {
    ModelSetType()
  }

  def buildFuncDef(funcDef: ModelSetFuncDefContext): ModelSetFuncDef = {
    ModelSetFuncDef()
  }

  def buildRef(ref: ModelSetRefContext): ModelSetRef = {
    ModelSetRef()
  }
}
