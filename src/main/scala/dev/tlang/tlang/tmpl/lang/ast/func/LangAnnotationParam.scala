package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast._
import tlang.core.{Null, Type, Value}
import tlang.internal._

case class LangAnnotationParam(context: Null, var name: Option[TmplID], var value: LangValueType[_])  extends TmplNode[LangAnnotationParam] with AstContext {
//  override def deepCopy(): LangAnnotationParam =
//    LangAnnotationParam(context,
//      if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
//      value.deepCopy().asInstanceOf[LangValueType[_]])

  override def getContext: Null = context

  override def getElement: LangAnnotationParam = this

  override def getType: Type = LangAnnotationParam.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAnnotationParam.modelName)),
    Some(List(
      //      BuildLang.createAttrNull(context, "name",
      //        if (name.isDefined) Null.of(name.get.toEntity) else Null.empty(),
      //        None
      //      ),
    ))
  )

}

object LangAnnotationParam {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}
