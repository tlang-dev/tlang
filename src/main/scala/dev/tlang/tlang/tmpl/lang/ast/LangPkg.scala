package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, TLangString}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import tlang.core.Null
import tlang.internal.{ContextContent, DeepCopy, TmplID, TmplNode}

case class LangPkg(context: Null[ContextContent], var parts: List[TmplID]) extends DeepCopy with TmplNode[LangPkg] {
  override def deepCopy(): LangPkg = {
    LangPkg(context, parts.map(_.deepCopy().asInstanceOf[TmplID]))
  }

  override def toEntity: EntityValue = {
    EntityValue(context,
      Some(ObjType(context, None, LangPkg.name)),
      Some(List(
        ComplexAttribute(context, Some("parts"),
          None, Operation(context, None, Right(ArrayValue(context, Some(parts.map(part => ComplexAttribute(context, None, None, Operation(context, None, Right(new TLangString(context, part.toString)))))))))
        ))
      ))
  }

  override def toModel: ModelSetEntity = LangPkg.model

  override def getContext: Null[ContextContent] = context
}

object LangPkg {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), name, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
  )))
  ModelSetAttribute(Null.empty(), Some("parts"), ModelSetType(Null.empty(), ArrayValue.getType))

}
