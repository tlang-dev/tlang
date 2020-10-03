package io.sorne.tlang.astbuilder

import io.sorne.tlang.TLangParser._
import io.sorne.tlang.ast.model._
import io.sorne.tlang.ast.model.`new`.{ModelNewAttribute, ModelNewEntity}
import io.sorne.tlang.ast.model.set._

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
    ModelNewEntity(newEntity.name.getText, if (newEntity.`type` != null) Some(newEntity.`type`.getText) else None, None, None)
  }

  def extractSetEntityAttrDefs(attrs: List[ModelAttributeContext]): Option[List[ModelNewAttribute]] = {
    if (attrs.nonEmpty) Some(attrs.map(attr => attr.modelSetValueType() match {
      case content@_ if content.modelSetType() != null => ModelSetAttribute(Utils.getText(attr.attr), buildType(content.modelSetType()))
      case content@_ if content.modelSetFuncDef() != null => ModelSetAttribute(Utils.getText(attr.attr), buildFuncDef(content.modelSetFuncDef()))
      case content@_ if content.modelSetRef() != null => ModelSetAttribute(Utils.getText(attr.attr), buildRef(content.modelSetRef()))
    }))
    else None
  }

  def buildSetEntity(setEntity: ModelSetEntityContext): ModelSetEntity = {
    ModelSetEntity(setEntity.name.getText, extractSetEntityAttrDefs(setEntity.params.asScala.toList), extractSetEntityAttrDefs(setEntity.attrs.asScala.toList))
  }

  def extractSetEntityAttrDefs(attrs: List[ModelSetAttributeContext]): Option[List[ModelSetAttribute]] = {
    if (attrs.nonEmpty) Some(attrs.map(attr => attr.modelSetValueType() match {
      case content@_ if content.modelSetType() != null => ModelSetAttribute(Utils.getText(attr.attr), buildType(content.modelSetType()))
      case content@_ if content.modelSetFuncDef() != null => ModelSetAttribute(Utils.getText(attr.attr), buildFuncDef(content.modelSetFuncDef()))
      case content@_ if content.modelSetRef() != null => ModelSetAttribute(Utils.getText(attr.attr), buildRef(content.modelSetRef()))
    }))
    else None
  }

  def buildType(setType: ModelSetTypeContext): ModelSetType = {
    ModelSetType(setType.`type`.getText, buildGeneric(setType.generic))
  }

  def buildGeneric(generic: ModelGenericContext): Option[ModelSetGeneric] = {
    if (generic != null && generic.types != null && !generic.isEmpty) Some(ModelSetGeneric(generic.types.asScala.map(buildType).toList))
    else None
  }

  def buildFuncDef(funcDef: ModelSetFuncDefContext): ModelSetFuncDef = {
    ModelSetFuncDef()
  }

  def buildRef(ref: ModelSetRefContext): ModelSetRef = {
    ModelSetRef(ref.ref.getText)
  }

}
