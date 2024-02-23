package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, TLangString}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID, TmplNode}

case class LangPkg(context: Null[ContextContent], var parts: List[TmplID]) extends TmplNode[LangPkg] {
  //  override def deepCopy(): LangPkg = {
  //    LangPkg(context, parts.map(_.deepCopy().asInstanceOf[TmplID]))
  //  }

  override def toEntity: EntityValue = {
    EntityValue(context,
      Some(ObjType(context, None, LangPkg.modelName)),
      Some(List(
        ComplexAttribute(context, Some("parts"),
          None, Operation(context, None, Right(ArrayValue(context, Some(parts.map(part => ComplexAttribute(context, None, None, Operation(context, None, Right(new TLangString(context, part.toString)))))))))
        ))
      ))
  }

  override def getContext: Null[ContextContent] = context


  override def getElement: LangPkg = this

  override def getType: Type = LangPkg.modelName
}

object LangPkg {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))
  ModelSetAttribute(Null.empty(), Some("parts"), ModelSetType(Null.empty(), ArrayValue.getType))

}
