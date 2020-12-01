package io.sorne.tlang.astbuilder

import io.sorne.tlang.TLangParser._
import io.sorne.tlang.ast.model._
import io.sorne.tlang.ast.model.set._

import scala.jdk.CollectionConverters._

object BuildModelBlock {

  def build(model: ModelBlockContext): ModelBlock = {
    val content = model.modelContents.asScala.map {
      case content@_ if content.assignVar() != null => BuildCommon.buildAssignVar(content.assignVar())
      case content@_ if content.modelSetEntity() != null => buildSetEntity(content.modelSetEntity())
    }.toList
    ModelBlock(if (content.nonEmpty) Some(content) else None)
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
    ModelSetRefCurrying(values.values.asScala.toList.map(buildModelSetRefValue))
  }

  def buildModelSetRefValue(value: ModelSetRefValueContext): ModelSetRefValue = {
    value match {
      case ref@_ if ref.modelSetRef() != null => buildRef(ref.modelSetRef())
      case valueType@_ if valueType.complexValueType() != null => BuildCommon.buildComplexValueType(None, valueType.complexValueType())
    }
  }

}
