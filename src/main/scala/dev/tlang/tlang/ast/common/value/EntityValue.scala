package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.{ManualType, ValueType}
import dev.tlang.tlang.ast.model.set.ModelSetRefValue
import dev.tlang.tlang.interpreter.ExecOperation
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel}
import tlang.core
import tlang.core.{Null, Type}
import tlang.internal.{ClassType, ContextContent}

case class EntityValue(context: Option[ContextContent],
                       var `type`: Option[ValueType],
                       attrs: Option[List[ComplexAttribute]] = None,
                       scope: Scope = Scope())
  extends PrimitiveValue[EntityValue] with ModelSetRefValue {

  override def getType: Type = if (`type`.isDefined) `type`.get.getContextType else ClassType.of(getClass)


  override def toEntity: AstEntity = AstEntity(context,
    Some(EntityValue.model),
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

  override def getContext: Option[ContextContent] = context

  override def getElement: EntityValue = this
}

object EntityValue {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
  )))

}