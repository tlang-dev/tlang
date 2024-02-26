package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ManualType, ObjType, ValueType}
import dev.tlang.tlang.ast.model.set.{ModelSetEntity, ModelSetRefValue}
import dev.tlang.tlang.interpreter.ExecOperation
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core
import tlang.core.func.FuncRet
import tlang.core.{Bool, Entity, Model, Null, Type, Value}
import tlang.internal.{AstContext, ClassType}

case class EntityValue(context: Null,
                       var `type`: Option[ValueType],
                       attrs: Option[List[ComplexAttribute]] = None,
                       scope: Scope = Scope())
  extends PrimitiveValue[EntityValue] with Entity with ModelSetRefValue with AstContext {

  override def getValue: Value = this

  override def getType: Type = if (`type`.isDefined) `type`.get.getContextType else ClassType.of(getClass)


  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, EntityValue.modelName)),
    Some(List())
  )

  def getAttrByName(name: core.String): Null = {
    if (attrs.isEmpty) Null.empty()
    else {
      val attr = attrs.get.find(attr => new core.String(attr.attr.orNull).isEqual(name).get())
      if (attr.isEmpty) Null.empty()
      else {
        val op = attr.get.value
        ExecOperation.run(op, Context(List(scope))) match {
          case Left(value) => Null.empty()
//          case Right(value) => Null.of(value.get.head)
        }
      }
    }
  }

  override def getContext: Null = context

  override def getAttr(name: core.String): Null = ???

  override def hasAttrs: Bool = ???

  override def exists(name: core.String): Bool = ???

  override def getAttr(index: core.Int): Null = ???

  override def exists(index: core.Int): Bool = ???

  override def getModel: Null = ???

  override def call(name: core.String, args: core.Array): FuncRet = ???

  override def call(index: core.Int, args: core.Array): FuncRet = ???

  override def getElement: EntityValue = ???
}

object EntityValue {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))

}