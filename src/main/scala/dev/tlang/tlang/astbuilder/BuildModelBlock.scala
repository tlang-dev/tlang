package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.TLangParser._
import dev.tlang.tlang.ast.model._
import dev.tlang.tlang.ast.model.set._
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource

import scala.jdk.CollectionConverters._

object BuildModelBlock {

  def build(resource: ContextResource, model: ModelBlockContext): ModelBlock = {
    val content = model.modelContents.asScala.map {
      case content@_ if content.assignVar() != null => BuildCommon.buildAssignVar(resource, content.assignVar())
      case content@_ if content.modelSetEntity() != null => buildSetEntity(resource, content.modelSetEntity())
    }.toList
    ModelBlock(addContext(resource, model), if (content.nonEmpty) Some(content) else None)
  }

  def buildSetEntity(resource: ContextResource, setEntity: ModelSetEntityContext): ModelSetEntity = {
    val ext = if (setEntity.ext != null && !setEntity.ext.isEmpty) Some(BuildCommon.buildObjType(resource, setEntity.ext)) else None
    ModelSetEntity(addContext(resource, setEntity), setEntity.name.getText, ext, extractSetEntityAttrDefs(resource, setEntity.params.asScala.toList), extractSetEntityAttrDefs(resource, setEntity.attrs.asScala.toList))
  }

  def extractSetEntityAttrDefs(resource: ContextResource, attrs: List[ModelSetAttributeContext]): Option[List[ModelSetAttribute]] = {
    if (attrs.nonEmpty) Some(attrs.map(attr => buildModeLSetValueType(resource, AstBuilderUtils.getText(attr.attr), attr.value)))
    else None
  }

  def buildModeLSetValueType(resource: ContextResource, attr: Option[String], value: ModelSetValueTypeContext): ModelSetAttribute = {
    value match {
      case content@_ if content.modelSetType() != null => ModelSetAttribute(addContext(resource, content.modelSetType()), attr, buildType(resource, content.modelSetType()))
      case content@_ if content.modelSetFuncDef() != null => ModelSetAttribute(addContext(resource, content.modelSetFuncDef()), attr, buildFuncDef(resource, content.modelSetFuncDef()))
      case content@_ if content.modelSetRef() != null => ModelSetAttribute(addContext(resource, content.modelSetRef()), attr, buildRef(resource, content.modelSetRef()))
      case content@_ if content.modelSetArray() != null => ModelSetAttribute(addContext(resource, content.modelSetArray()), attr, ModelSetArray(addContext(resource, content.modelSetArray()), content.modelSetArray().array.getText))
      case impl@_ if impl.modelSetImpl() != null => ModelSetAttribute(addContext(resource, impl.modelSetImpl()), attr, ModelSetImpl(addContext(resource, impl), None, extractSetEntityAttrDefs(resource, impl.modelSetImpl().attrs.asScala.toList)))
      case impl@_ if impl.modelSetImplArray() != null => ModelSetAttribute(addContext(resource, impl.modelSetImplArray()), attr, ModelSetImplArray(addContext(resource, impl), None))
    }
  }

  def buildType(resource: ContextResource, setType: ModelSetTypeContext): ModelSetType = {
    ModelSetType(addContext(resource, setType), setType.`type`.getText)
  }

  def buildFuncDef(resource: ContextResource, funcDef: ModelSetFuncDefContext): ModelSetFuncDef = {
    ModelSetFuncDef(addContext(resource, funcDef),
      if (funcDef.paramTypes != null && !funcDef.paramTypes.isEmpty) Some(funcDef.paramTypes.asScala.toList.map(param => buildModeLSetValueType(resource, None, param))) else None,
      if (funcDef.retTypes != null && !funcDef.retTypes.isEmpty) Some(funcDef.retTypes.asScala.toList.map(ret => buildModeLSetValueType(resource, None, ret))) else None)
  }

  def buildRef(resource: ContextResource, ref: ModelSetRefContext): ModelSetRef = {
    ModelSetRef(addContext(resource, ref), ref.refs.asScala.toList.map(_.getText), if (ref.currying != null && !ref.currying.isEmpty) Some(ref.currying.asScala.toList.map(curry => buildModelSetRefCurrying(resource, curry))) else None)
  }

  def buildModelSetRefCurrying(resource: ContextResource, values: ModelSetRefCurryingContext): ModelSetRefCurrying = {
    ModelSetRefCurrying(addContext(resource, values), values.values.asScala.toList.map(value => buildModelSetRefValue(resource, value)))
  }

  def buildModelSetRefValue(resource: ContextResource, value: ModelSetRefValueContext): ModelSetRefValue = {
    value match {
      case ref@_ if ref.modelSetRef() != null => buildRef(resource, ref.modelSetRef())
      case valueType@_ if valueType.operation() != null => BuildCommon.buildOperation(resource, None, valueType.operation())
    }
  }

}
