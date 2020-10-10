package io.sorne.tlang.astbuilder

import io.sorne.tlang.TLangParser._
import io.sorne.tlang.ast.model._
import io.sorne.tlang.ast.model.`new`._
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
    ModelNewEntity(newEntity.name.getText, buildNewEntityValue(newEntity.entity))
  }

  def buildNewEntityValue(newEntity: ModelNewEntityValueContext): ModelNewEntityValue = {
    ModelNewEntityValue(if (newEntity.`type` != null) Some(newEntity.`type`.getText) else None, extractNewEntityAttrDefs(newEntity.attrs.asScala.toList), extractNewEntityAttrDefs(newEntity.decl.asScala.toList))
  }

  def extractNewEntityAttrDefs(attrs: List[ModelValueTypeContext]): Option[List[ModelNewAttribute]] = {
    if (attrs.nonEmpty) Some(attrs.map {
      case attr@(content@_) if content.modelAttribute() != null => ModelNewAttribute(Utils.getText(attr.modelAttribute().attr), buildNewEntityAttribute(content.modelAttribute()))
      case attr@(content@_) if content.modelEntityAsAttribute() != null => ModelNewAttribute(Utils.getText(attr.modelEntityAsAttribute().attr), buildNewEntityAsAttribute(content.modelEntityAsAttribute()))
      case attr@(content@_) if content.modelTbl() != null => ModelNewAttribute(Utils.getText(attr.modelTbl().attr), buildNewEntityTbl(content.modelTbl()))
    })
    else None
  }

  def buildNewEntityAttribute(attr: ModelAttributeContext): ModelNewPrimitiveValue = {
    ModelNewPrimitiveValue(if (attr.attr != null) Some(attr.attr.getText) else None, attr.value.getText)
  }

  def buildNewEntityAsAttribute(attr: ModelEntityAsAttributeContext): ModelNewEntityValue = {
    buildNewEntityValue(attr.value)
  }


  def buildNewEntityTbl(attr: ModelTblContext): ModelNewArrayValue = {
    ModelNewArrayValue(if (attr.attr != null) Some(attr.attr.getText) else None, if (attr.elms != null) extractNewEntityAttrDefs(attr.elms.asScala.toList) else None)
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
