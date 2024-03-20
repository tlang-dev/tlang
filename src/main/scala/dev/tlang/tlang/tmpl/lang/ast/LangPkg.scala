package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.value.ArrayValue
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetType}
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl._
import tlang.core.Type
import tlang.internal.ContextContent

case class LangPkg(context: Option[ContextContent], var parts: List[TmplID]) extends AstTmplNode {
  //  override def deepCopy(): LangPkg = {
  //    LangPkg(context, parts.map(_.deepCopy().asInstanceOf[TmplID]))
  //  }

  override def toEntity: AstEntity = {
    AstEntity(context,
      Some(LangPkg.model),
      Some(List(
        BuildAstTmpl.createAttrList(context, "parts", parts.map(_.toEntity))
      )))
  }

  override def getContext: Option[ContextContent] = context


  override def getElement: LangPkg = this

  override def getType: Type = LangPkg.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = LangPkg.model
}

object LangPkg {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
  )))
  ModelSetAttribute(None, Some("parts"), ModelSetType(None, ArrayValue.getType))

}
