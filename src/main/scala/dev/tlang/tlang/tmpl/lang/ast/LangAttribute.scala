package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID, TmplNode}

case class LangAttribute(context: Null, var attr: Option[TmplID], var `type`: Option[LangType], var value: LangOperation) extends TmplNode[LangAttribute] {
//  override def deepCopy(): LangAttribute = LangAttribute(context,
//    if (attr.isDefined) Some(attr.get.getElement.deepCopy().asInstanceOf[TmplID]) else None,
//    if (`type`.isDefined) Some(`type`.get.getElement.deepCopy()) else None,
//    value.deepCopy()
//  )

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangAttribute.modelName)),
    Some(List(
      //      BuildLang.createAttrNull(context, "attr",
      //        attr,
      //        None
      //      ),
      //      BuildLang.createAttrNull(context, "tType",
      //        `type`,
      //        None
      //      ),
//      BuildLang.createAttrEntity(context, "value", value.toEntity),
    ))
  )

//  override def toModel: ModelSetEntity = LangAttribute.model

  override def getElement: LangAttribute = this

  override def getType: Type = LangAttribute.modelName

  override def getContext: Null = context
}

object LangAttribute {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("attr"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("tType"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), LangOperation.modelType)),
  )))
}
