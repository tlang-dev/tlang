package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.{ModelSetEntity, ModelSetRefValue}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.interpreter.{ExecError, NotImplemented, Value}
import dev.tlang.tlang.tmpl.lang.ast.{TmplLangAst, TmplValueAst}

case class EntityValue(context: Option[ContextContent],
                       var `type`: Option[ValueType],
                       attrs: Option[List[ComplexAttribute]] = None,
                       scope: Scope = Scope())
  extends PrimitiveValue[EntityValue] with ModelSetRefValue with AstContext {

  override def getElement: EntityValue = this

  override def getType: String = if (`type`.isDefined) `type`.get.getContextType else getClass.getName

  override def compareTo(value: Value[EntityValue]): Int = 0

  override def add(value: PrimitiveValue[EntityValue]): Either[ExecError, EntityValue] = Left(NotImplemented(context = context))

  override def subtract(value: PrimitiveValue[EntityValue]): Either[ExecError, EntityValue] = Left(NotImplemented(context = context))

  override def multiply(value: PrimitiveValue[EntityValue]): Either[ExecError, EntityValue] = Left(NotImplemented(context = context))

  override def divide(value: PrimitiveValue[EntityValue]): Either[ExecError, EntityValue] = Left(NotImplemented(context = context))

  override def modulo(value: PrimitiveValue[EntityValue]): Either[ExecError, EntityValue] = Left(NotImplemented(context = context))

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): EntityValue = EntityValue(context, `type`, attrs, scope)

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, TmplValueAst.langEntity.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}
