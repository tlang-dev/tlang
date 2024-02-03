package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, TLangString}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.{DeepCopy, TmplNode}

case class LangPkg(context: Option[ContextContent], var parts: List[TmplID]) extends DeepCopy with TmplNode[LangPkg] {
  override def deepCopy(): LangPkg = {
    LangPkg(context, parts.map(_.deepCopy().asInstanceOf[TmplID]))
  }

  override def compareTo(value: Value[LangPkg]): Int = 0

  override def getElement: LangPkg = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

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
}

object LangPkg {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("parts"), ModelSetType(None, ArrayValue.getType)),
  )))
}
