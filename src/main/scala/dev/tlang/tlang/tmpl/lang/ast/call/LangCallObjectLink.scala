package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.value.{EntityValue, TLangString}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class LangCallObjectLink(context: Null, var link: String = ".", var call: LangCallObjType[_]) extends TmplNode[LangCallObjectLink] {
  //  override def deepCopy(): LangCallObjectLink = LangCallObjectLink(context, link, call.deepCopy().asInstanceOf[LangCallObjType[_]])

  override def getType: Type = LangCallObjectLink.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallObjectLink.modelName)),
    Some(List(
      BuildLang.createAttrStr(context, "link", link),
      BuildLang.createAttrEntity(context, "call", call.toEntity),
    ))
  )

  override def getContext: Null = context

  override def getElement: LangCallObjectLink = this
}

object LangCallObjectLink {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("link"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("call"), ModelSetType(Null.empty(), LangCallObjType.modelName)),
  )))
}
