package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangVar(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var props: Option[LangProp] = None, var name: TmplID, var `type`: Option[LangType], var value: Option[LangOperation], isOptional: Boolean) extends LangExpression[LangVar] with AstContext {
  override def deepCopy(): LangVar = LangVar(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    name.deepCopy().asInstanceOf[TmplID],
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    if (value.isDefined) Some(value.get.deepCopy()) else None,
    isOptional
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangVar]): Int = 0

  override def getElement: LangVar = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangVar.name)),
    Some(List(
      BuildLang.createAttrNull(context, "annots",
        if (annots.isDefined) Some(ArrayValue(context, Some(annots.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createAttrNull(context, "props",
        if (props.isDefined) Some(props.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrNull(context, "tType",
        if (`type`.isDefined) Some(`type`.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrNull(context, "value",
        if (value.isDefined) Some(value.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrBool(context, "isOptional", isOptional),
    ))
  )

  override def toModel: ModelSetEntity = LangVar.model
}

object LangVar {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("annots"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("props"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("name"), ModelSetType(None, TmplID.name)),
    ModelSetAttribute(None, Some("tType"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("value"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("isOptional"), ModelSetType(None, TLangBool.getType)),
  )))
}
