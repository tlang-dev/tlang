package dev.tlang.tlang.tmpl.lang.ast.func

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, NullValue}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.common.ast.TmplID
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class LangFunc(context: Option[ContextContent], var annots: Option[List[LangAnnotation]] = None, var props: Option[LangProp] = None, var preNames: Option[List[TmplID]] = None, var name: TmplID, var curries: Option[List[LangFuncParam]], var content: Option[LangExprContent[_]],
                    var ret: Option[List[LangType]] = None, postPros: Option[LangProp] = None) extends LangExpression[LangFunc] with LangContent[LangFunc] with AstContext {
  override def deepCopy(): LangFunc = LangFunc(context,
    if (annots.isDefined) Some(annots.get.map(_.deepCopy())) else None,
    if (props.isDefined) Some(props.get.deepCopy()) else None,
    if (preNames.isDefined) Some(preNames.get.map(_.deepCopy().asInstanceOf[TmplID])) else None,
    name.deepCopy().asInstanceOf[TmplID],
    if (curries.isDefined) Some(curries.get.map(_.deepCopy())) else None,
    if (content.isDefined) Some(content.get.deepCopy().asInstanceOf[LangExprContent[_]]) else None,
    if (ret.isDefined) Some(ret.get.map(_.deepCopy())) else None,
    if (postPros.isDefined) Some(postPros.get.deepCopy()) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[LangFunc]): Int = 0

  override def getElement: LangFunc = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, LangFunc.name)),
    Some(List(
      BuildLang.createAttrNull(context, "annots",
        if (annots.isDefined) Some(ArrayValue(context, Some(annots.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createAttrNull(context, "props",
        if (props.isDefined) Some(props.get.toEntity) else None,
        None
      ),
      BuildLang.createAttrNull(context, "preNames",
        if (preNames.isDefined) Some(ArrayValue(context, Some(preNames.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createAttrEntity(context, "name", name.toEntity),
      BuildLang.createAttrNull(context, "curries",
        if (curries.isDefined) Some(ArrayValue(context, Some(curries.get.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value.toEntity))))))) else None,
        None
      ),
      BuildLang.createAttrEntity(context, "content", content.map(_.toEntity).getOrElse(EntityValue(context, None, None))),
    ))
  )

  override def toModel: ModelSetEntity = LangFunc.model
}

object LangFunc {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val model: ModelSetEntity = ModelSetEntity(None, name, Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("annots"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("props"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("preNames"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("name"), ModelSetType(None, TmplID.name)),
    ModelSetAttribute(None, Some("curries"), ModelSetType(None, NullValue.name)),
    ModelSetAttribute(None, Some("content"), ModelSetType(None, NullValue.name)),
  )))
}
