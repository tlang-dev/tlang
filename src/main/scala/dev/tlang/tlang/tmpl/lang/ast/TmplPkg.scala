package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, TLangString}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplPkg(context: Option[ContextContent], var parts: List[TmplID]) extends DeepCopy with TmplNode[TmplPkg] {
  override def deepCopy(): TmplPkg = {
    TmplPkg(context, parts.map(_.deepCopy().asInstanceOf[TmplID]))
  }

  override def compareTo(value: Value[TmplPkg]): Int = 0

  override def getElement: TmplPkg = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = {
    EntityValue(context,
      Some(ObjType(context, None, TmplLangAst.langPkg.name)),
      Some(List(
        ComplexAttribute(context, Some("parts"),
          None, Operation(context, None, Right(ArrayValue(context, Some(parts.map(part => ComplexAttribute(context, None, None, Operation(context, None, Right(new TLangString(context, part.toString)))))))))
        ))
      ))
  }

  override def toModel: ModelSetEntity = ModelSetEntity(None, getType, Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}