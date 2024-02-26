package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type, Value}
import tlang.internal.{AstContext, ContextContent, TmplID}

case class LangVar(context: Null, var annots: Option[List[LangAnnotation]] = None, var props: Option[LangProp] = None, var name: TmplID, var `type`: Option[LangType], var value: Option[LangOperation], isOptional: Boolean) extends LangExpression[LangVar] with AstContext {
//  override def deepCopy(): LangVar = LangVar(context,
//    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
//    if (props.isDefined) Some(props.get.deepCopy()) else None,
//    name.deepCopy().asInstanceOf[TmplID],
//    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
//    if (value.isDefined) Some(value.get.deepCopy()) else None,
//    isOptional
//  )

  override def getContext: Null = context

  override def getElement: LangVar = this

  override def getType: Type = LangVar.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangVar.modelName)),
    Some(List(
//      BuildLang.createAttrNull(context, "annots",
//        if (annots.isDefined) Some(ArrayValue(context, Some(annots.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
//        None
//      ),
//      BuildLang.createAttrNull(context, "props",
//        if (props.isDefined) Null.of(props.get.toEntity) else Null.empty(),
//        None
//      ),
      BuildLang.createAttrEntity(context, "name", name.toEntity),
//      BuildLang.createAttrNull(context, "tType",
//        if (`type`.isDefined) Null.of(`type`.get.toEntity) else Null.empty(),
//        None
//      ),
//      BuildLang.createAttrNull(context, "value",
//        if (value.isDefined) Null.of(value.get.toEntity) else Null.empty(),
//        None
//      ),
      BuildLang.createAttrBool(context, "isOptional", isOptional),
    ))
  )

}

object LangVar {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(LangModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(Null.empty(), Some("annots"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("props"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TmplID.TYPE)),
    ModelSetAttribute(Null.empty(), Some("tType"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("value"), ModelSetType(Null.empty(), Null.TYPE)),
    ModelSetAttribute(Null.empty(), Some("isOptional"), ModelSetType(Null.empty(), TLangBool.getType)),
  )))
}
