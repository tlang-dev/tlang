package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.{LangExprContent, LangExpression, LangModel}
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class LangAnonFunc(context: Null, var curries: Option[List[LangFuncParam]], var content: LangExprContent[_]) extends LangExpression[LangAnonFunc] {

  override def getType: Type = LangAnonFunc.modelName

  override def getContext: Null = context

  //  override def deepCopy(): LangAnonFunc = LangAnonFunc(context,
  //    if (curries.isDefined) Some(curries.get.map(curry => curry.deepCopy())) else None,
  //    content.deepCopy().asInstanceOf[LangExprContent[_]])


  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAnonFunc.modelName)),
    Some(List(
      //      BuildLang.createAttrNull(context, "curries",
      //        if (curries.isDefined) Some(ArrayValue(context, Some(curries.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
      //        None
      //      ),
      BuildLang.createAttrEntity(context, "content", content.toEntity),
    ))
  )

  override def getElement: LangAnonFunc = this
}

object LangAnonFunc {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("curries"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("content"), ModelSetType(Null.empty(), LangExprContent.modelName)),
  )))
}
