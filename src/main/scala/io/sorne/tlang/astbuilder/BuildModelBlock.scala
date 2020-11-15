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
      case attr@(content@_) if content.modelArray() != null => ModelNewAttribute(Utils.getText(attr.modelArray().attr), buildNewEntityArray(content.modelArray()))
    })
    else None
  }

  def buildNewEntityAttribute(attr: ModelAttributeContext): ModelNewPrimitiveValue = {
    ModelNewPrimitiveValue(if (attr.attr != null) Some(attr.attr.getText) else None, attr.value.getText)
  }

  def buildNewEntityAsAttribute(attr: ModelEntityAsAttributeContext): ModelNewEntityValue = {
    buildNewEntityValue(attr.value)
  }

  def buildNewEntityArray(attr: ModelArrayContext): ModelNewArrayValue = {
    ModelNewArrayValue(if (attr.attr != null) Some(attr.attr.getText) else None, if (attr.elms != null) extractNewEntityAttrDefs(attr.elms.asScala.toList) else None)
  }

  def buildSetEntity(setEntity: ModelSetEntityContext): ModelSetEntity = {
    ModelSetEntity(setEntity.name.getText, extractSetEntityAttrDefs(setEntity.params.asScala.toList), extractSetEntityAttrDefs(setEntity.attrs.asScala.toList))
  }

  def extractSetEntityAttrDefs(attrs: List[ModelSetAttributeContext]): Option[List[ModelSetAttribute]] = {
    if (attrs.nonEmpty) Some(attrs.map(attr => buildModeLSetValueType(Utils.getText(attr.attr), attr.value)))
    else None
  }

  def buildModeLSetValueType(attr: Option[String], value: ModelSetValueTypeContext): ModelSetAttribute = {
    value match {
      case content@_ if content.modelSetType() != null => ModelSetAttribute(attr, buildType(content.modelSetType()))
      case content@_ if content.modelSetFuncDef() != null => ModelSetAttribute(attr, buildFuncDef(content.modelSetFuncDef()))
      case content@_ if content.modelSetRef() != null => ModelSetAttribute(attr, buildRef(content.modelSetRef()))
      case content@_ if content.modelSetArray() != null => ModelSetAttribute(attr, ModelSetArray(content.modelSetArray().array.getText))
    }
  }

  def buildType(setType: ModelSetTypeContext): ModelSetType = {
    ModelSetType(setType.`type`.getText)
  }

  def buildFuncDef(funcDef: ModelSetFuncDefContext): ModelSetFuncDef = {
    ModelSetFuncDef(
      if (funcDef.paramTypes != null && !funcDef.paramTypes.isEmpty) Some(funcDef.paramTypes.asScala.toList.map(param => buildModeLSetValueType(None, param))) else None,
      if (funcDef.retTypes != null && !funcDef.retTypes.isEmpty) Some(funcDef.retTypes.asScala.toList.map(ret => buildModeLSetValueType(None, ret))) else None)
  }

  def buildRef(ref: ModelSetRefContext): ModelSetRef = {
    ModelSetRef(ref.refs.asScala.toList.map(_.getText), if (ref.currying != null && !ref.currying.isEmpty) Some(ref.currying.asScala.toList.map(buildModelSetRefCurrying)) else None)
  }

  def buildModelSetRefCurrying(values: ModelSetRefCurryingContext): ModelSetRefCurrying = {
    ModelSetRefCurrying(values.values.asScala.toList.map {
      case value@_ if value.modelAttribute() != null => ModelNewAttribute(Utils.getText(value.modelAttribute().attr), buildNewEntityAttribute(value.modelAttribute()))
      case value@_ if value.modelArray() != null => ModelNewAttribute(Utils.getText(value.modelArray().attr), buildNewEntityArray(value.modelArray()))
      case value@_ if value.modelEntityAsAttribute() != null => ModelNewAttribute(Utils.getText(value.modelEntityAsAttribute().attr), buildNewEntityAsAttribute(value.modelEntityAsAttribute()))
      case value@_ if value.modelSetRef() != null => ModelSetRef(value.modelSetRef().refs.asScala.toList.map(_.getText), if (value.modelSetRef().currying != null) Some(value.modelSetRef().currying.asScala.toList.map(buildModelSetRefCurrying)) else None)
    })
  }

}
