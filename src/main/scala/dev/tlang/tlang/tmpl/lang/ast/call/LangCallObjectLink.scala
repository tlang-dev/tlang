package dev.tlang.tlang.tmpl.lang.ast.call

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{EntityValue, TLangString}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.{ContextContent, DeepCopy, TmplNode}

case class LangCallObjectLink(context: Null[ContextContent], var link: String = ".", var call: LangCallObjType[_]) extends DeepCopy with TmplNode[LangCallObjectLink] {
  override def deepCopy(): LangCallObjectLink = LangCallObjectLink(context, link, call.deepCopy().asInstanceOf[LangCallObjType[_]])

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangCallObjectLink.name)),
    Some(List(
      BuildLang.createAttrStr(context, "link", link),
      BuildLang.createAttrEntity(context, "call", call.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = LangCallObjectLink.model

  override def getContext: Null[ContextContent] = context
}

object LangCallObjectLink {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("link"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("call"), ModelSetType(Null.empty(), LangCallObjType.name)),
  )))
}
