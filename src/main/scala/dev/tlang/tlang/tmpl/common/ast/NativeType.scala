package dev.tlang.tlang.tmpl.common.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode, AstValue}
import tlang.core.Type
import tlang.internal.ContextContent

case class NativeType[T](context: Option[ContextContent], statement: T) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context, None, None)

  //  override def toModel: ModelSetEntity = NativeType.model

  override def getType: Type = NativeType.modelName

  //  override def deepCopy(): NativeType[_] = NativeType(context, statement)

  override def getContext: Option[ContextContent] = context

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = NativeType.model

  override def getElement: AstValue = this
}

object NativeType {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, None, None, None)
}
