package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.{ModelSetEntity, ModelSetRefValue}
import dev.tlang.tlang.interpreter.ExecOperation
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.{Bool, Model, Null, Type, Value}
import tlang.internal.{AstContext, ClassType, ContextContent, Element}
import tlang.{Entity, core}

case class EntityValue(context: Null[ContextContent],
                       var `type`: Option[ValueType],
                       attrs: Option[List[ComplexAttribute]] = None,
                       scope: Scope = Scope())
  extends PrimitiveValue[EntityValue] with Entity with ModelSetRefValue with AstContext {

  override def getElement: EntityValue = this

  override def getType: Type = if (`type`.isDefined) `type`.get.getContextType else ClassType.of(getClass)


  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, EntityValue.modelName)),
    Some(List())
  )

  def getAttrByName(name: core.String): Null[core.Value[_]] = {
    if (attrs.isEmpty) Null.empty().asInstanceOf[Null[core.Value[_]]]
    else {
      val attr = attrs.get.find(attr => new core.String(attr.attr.orNull).isEqual(name).get())
      if (attr.isEmpty) Null.empty().asInstanceOf[Null[core.Value[_]]]
      else {
        val op = attr.get.value
        ExecOperation.run(op, Context(List(scope))) match {
          case Left(value) => Null.empty().asInstanceOf[Null[core.Value[_]]]
//          case Right(value) => Null.of(value.get.head)
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

  override def getAttr[T <: Value[T]](name: core.String): Null[T] = {
    Null.empty()
  }
}

object EntityValue {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))

}