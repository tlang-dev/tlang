package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.{ModelSetEntity, ModelSetRefValue}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.interpreter.{ExecError, ExecOperation, NotImplemented}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.{Bool, Model, Null}
import tlang.internal.{AstContext, ContextContent}
import tlang.{Entity, core}

case class EntityValue(context: Null[ContextContent],
                       var `type`: Option[ValueType],
                       attrs: Option[List[ComplexAttribute]] = None,
                       scope: Scope = Scope())
  extends PrimitiveValue[EntityValue] with Entity with ModelSetRefValue with AstContext {

  override def getElement: EntityValue = this

  override def getType: String = if (`type`.isDefined) `type`.get.getContextType else getClass.getSimpleName

  override def add(value: PrimitiveValue[EntityValue]): Either[ExecError, EntityValue] = Left(NotImplemented(context = context))

  override def subtract(value: PrimitiveValue[EntityValue]): Either[ExecError, EntityValue] = Left(NotImplemented(context = context))

  override def multiply(value: PrimitiveValue[EntityValue]): Either[ExecError, EntityValue] = Left(NotImplemented(context = context))

  override def divide(value: PrimitiveValue[EntityValue]): Either[ExecError, EntityValue] = Left(NotImplemented(context = context))

  override def modulo(value: PrimitiveValue[EntityValue]): Either[ExecError, EntityValue] = Left(NotImplemented(context = context))

  override def deepCopy(): EntityValue = EntityValue(context, `type`, attrs, scope)

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, EntityValue.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))

  override def getAttr(name: core.String): Null[core.Value[_]] = {
    if (attrs.isEmpty) Null.empty()
    else {
      val attr = attrs.get.find(attr => new core.String(attr.attr.orNull).isEqual(name).get())
      if (attr.isEmpty) Null.empty()
      else {
        val op = attr.get.value
        ExecOperation.run(op, Context(List(scope))) match {
          case Left(value) => Null.empty()
          case Right(value) => Null.of(value.get.head)
        }
      }
    }
  }

  override def exists(name: core.String): Bool = {
    if (attrs.isEmpty) Bool.FALSE
    else {
      val attr = attrs.get.find(attr => new core.String(attr.attr.orNull).isEqual(name).get())
      if (attr.isEmpty) Bool.FALSE
      else Bool.TRUE
    }
  }

  override def hasAttrs: Bool = if (attrs.isEmpty) Bool.FALSE else Bool.TRUE

  override def getModel: Null[Model] = Null.empty()

  override def getContext: Null[ContextContent] = context
}

object EntityValue {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))

}