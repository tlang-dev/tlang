package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.value.ArrayValue
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetType}
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID, TmplNode}

case class LangPkg(context: Option[ContextContent], var parts: List[TmplID]) extends TmplNode[LangPkg] {
  //  override def deepCopy(): LangPkg = {
  //    LangPkg(context, parts.map(_.deepCopy().asInstanceOf[TmplID]))
  //  }

  override def toEntity: AstEntity = {
    AstEntity(context,
      Some(LangPkg.model),
      Some(List(
//        BuildAstTmpl.createAttrList(context, "parts", parts.map(_.toEntity))
      )))
  }

  override def getContext: Option[ContextContent] = context


  override def getElement: LangPkg = this

  override def getType: Type = LangPkg.modelName
}

object LangPkg {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(LangModel.langNode), None, Some(List(
  )))
  ModelSetAttribute(Null.empty(), Some("parts"), ModelSetType(Null.empty(), ArrayValue.getType))

}
