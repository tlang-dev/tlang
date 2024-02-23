package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.doc.ast.DocModel
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplID, TmplNode}

case class LangParam(context: Null[ContextContent], var annots: Option[List[LangAnnotation]] = None, var name: TmplID, var `type`: Option[LangType]) extends TmplNode[LangParam] {
//  override def deepCopy(): LangParam = LangParam(context,
//    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
//    name.deepCopy().asInstanceOf[TmplID],
//    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None)


  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangParam.modelName)),
    Some(List(
//      BuildLang.createAttrNull(context, "annots",
//        if (annots.isDefined) Some(ArrayValue(context, Some(annots.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
//        None
//      ),
      BuildLang.createAttrEntity(context, "name", name.toEntity),
//      BuildLang.createAttrNull(context, "tType",
//        if (`type`.isDefined) Some(`type`.get.toEntity) else None,
//        None
//      )
    ))
  )

  override def getContext: Null[ContextContent] = context

  override def getElement: LangParam = this

  override def getType: Type = LangParam.modelName
}

object LangParam {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("annots"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TmplID.TYPE)),
    ModelSetAttribute(Null.empty(), Some("tType"), ModelSetType(Null.empty(), Null.TYPE)),
  )))
}